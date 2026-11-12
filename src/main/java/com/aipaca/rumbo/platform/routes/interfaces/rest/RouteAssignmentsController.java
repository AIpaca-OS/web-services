package com.aipaca.rumbo.platform.routes.interfaces.rest;

import com.aipaca.rumbo.platform.routes.application.commandservices.RouteCommandService;
import com.aipaca.rumbo.platform.routes.application.queryservices.RouteQueryService;
import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetRouteByIdQuery;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.AssignStudentResource;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteAssignmentResource;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteResource;
import com.aipaca.rumbo.platform.routes.interfaces.rest.transform.AssignStudentToRouteCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.routes.interfaces.rest.transform.ReplaceRouteAssignmentsCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.routes.interfaces.rest.transform.RouteAssignmentResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.routes.interfaces.rest.transform.RouteResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * REST controller that exposes the student-to-stop assignments of a route.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/routes/{routeId}/assignments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Routes", description = "School route planning endpoints")
public class RouteAssignmentsController {
    private final RouteCommandService routeCommandService;
    private final RouteQueryService routeQueryService;

    /**
     * Constructor
     *
     * @param routeCommandService The {@link RouteCommandService} instance
     * @param routeQueryService   The {@link RouteQueryService} instance
     */
    public RouteAssignmentsController(RouteCommandService routeCommandService, RouteQueryService routeQueryService) {
        this.routeCommandService = routeCommandService;
        this.routeQueryService = routeQueryService;
    }

    /**
     * Get the assignments of a route
     *
     * @param routeId The route ID
     * @return A list of {@link RouteAssignmentResource} resources
     */
    @GetMapping
    @Operation(summary = "Get route assignments", description = "Lists the students assigned to each stop of the route.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignments found",
                    content = @Content(schema = @Schema(implementation = RouteAssignmentResource.class))),
            @ApiResponse(responseCode = "404", description = "Route not found")
    })
    public ResponseEntity<?> getRouteAssignments(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId) {
        var route = routeQueryService.handle(new GetRouteByIdQuery(routeId));
        if (route.isEmpty()) {
            var error = ApplicationError.notFound("Route", routeId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(route.get().getAssignments().stream()
                .map(RouteAssignmentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Assign a student to a stop of the route
     *
     * @param routeId  The route ID
     * @param resource The {@link AssignStudentResource} instance
     * @return A {@link RouteAssignmentResource} resource for the created assignment
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Assign student", description = "Assigns a student to a stop of a configurable route.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assignment created",
                    content = @Content(schema = @Schema(implementation = RouteAssignmentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Route not found"),
            @ApiResponse(responseCode = "409", description = "Student already assigned"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")
    })
    public ResponseEntity<?> assignStudent(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId,
            @Valid @RequestBody AssignStudentResource resource) {
        var command = AssignStudentToRouteCommandFromResourceAssembler.toCommandFromResource(routeId, resource);
        var result = routeCommandService.handle(command)
                .flatMap(assignmentId -> routeQueryService.handle(new GetRouteByIdQuery(routeId))
                        .flatMap(route -> findAssignment(route, assignmentId))
                        .<Result<RouteAssignment, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Assignment", assignmentId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RouteAssignmentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Replace every assignment of the route
     *
     * @param routeId   The route ID
     * @param resources The complete list of {@link AssignStudentResource} instances
     * @return A {@link RouteResource} resource for the updated route
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Synchronize route assignments", description = "Atomically replaces every assignment of a configurable route.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignments saved",
                    content = @Content(schema = @Schema(implementation = RouteResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Route not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")
    })
    public ResponseEntity<?> replaceRouteAssignments(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId,
            @RequestBody List<@Valid AssignStudentResource> resources) {
        var command = ReplaceRouteAssignmentsCommandFromResourceAssembler.toCommandFromResources(routeId, resources);
        var result = routeCommandService.handle(command)
                .flatMap(id -> routeQueryService.handle(new GetRouteByIdQuery(id))
                        .<Result<Route, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Route", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RouteResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    private static java.util.Optional<RouteAssignment> findAssignment(Route route, Long assignmentId) {
        return route.getAssignments().stream()
                .filter(assignment -> Objects.equals(assignment.getId(), assignmentId))
                .findFirst();
    }
}
