package com.aipaca.rumbo.platform.routes.interfaces.rest;

import com.aipaca.rumbo.platform.routes.application.commandservices.RouteCommandService;
import com.aipaca.rumbo.platform.routes.application.queryservices.RouteQueryService;
import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.commands.DeleteRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.commands.PublishRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetAllRoutesByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetAllRoutesQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetRouteByIdQuery;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteStatus;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.*;
import com.aipaca.rumbo.platform.routes.interfaces.rest.transform.*;
import com.aipaca.rumbo.platform.shared.interfaces.rest.resources.MessageResource;
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

/**
 * REST controller that exposes route planning endpoints: stops, schedules and publication.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/routes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Routes", description = "School route planning endpoints")
public class RoutesController {
    private final RouteCommandService routeCommandService;
    private final RouteQueryService routeQueryService;

    /**
     * Constructor
     *
     * @param routeCommandService The {@link RouteCommandService} instance
     * @param routeQueryService   The {@link RouteQueryService} instance
     */
    public RoutesController(RouteCommandService routeCommandService, RouteQueryService routeQueryService) {
        this.routeCommandService = routeCommandService;
        this.routeQueryService = routeQueryService;
    }

    /**
     * Create a new route
     *
     * @param resource The {@link CreateRouteResource} instance
     * @return A {@link RouteResource} resource for the created route
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new route", description = "Creates a route in CONFIGURABLE status with ordered stops and an optional schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Route created",
                    content = @Content(schema = @Schema(implementation = RouteResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createRoute(@Valid @RequestBody CreateRouteResource resource) {
        var createRouteCommand = CreateRouteCommandFromResourceAssembler.toCommandFromResource(resource);
        return toRouteResponse(routeCommandService.handle(createRouteCommand), HttpStatus.CREATED);
    }

    /**
     * Get all routes, or the routes of a driver
     *
     * @param driverProfileId The driver profile ID, or null for every route
     * @return A list of {@link RouteResource} resources
     */
    @GetMapping
    @Operation(summary = "Get routes", description = "Retrieves every route, or only those of a driver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Routes found",
                    content = @Content(schema = @Schema(implementation = RouteResource.class)))
    })
    public ResponseEntity<List<RouteResource>> getAllRoutes(
            @RequestParam(required = false)
            @Parameter(description = "Driver profile unique identifier; when present, only the routes of that driver", example = "2")
            Long driverProfileId) {
        var routes = driverProfileId == null
                ? routeQueryService.handle(new GetAllRoutesQuery())
                : routeQueryService.handle(new GetAllRoutesByDriverProfileIdQuery(driverProfileId));
        return ResponseEntity.ok(routes.stream().map(RouteResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get a route by ID
     *
     * @param routeId The route ID
     * @return A {@link RouteResource} resource for the route
     */
    @GetMapping("/{routeId}")
    @Operation(summary = "Get route by ID", description = "Retrieves a route with its stops, schedule and assigned students.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route found",
                    content = @Content(schema = @Schema(implementation = RouteResource.class))),
            @ApiResponse(responseCode = "404", description = "Route not found")
    })
    public ResponseEntity<?> getRouteById(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId) {
        var route = routeQueryService.handle(new GetRouteByIdQuery(routeId));
        if (route.isEmpty()) {
            var error = ApplicationError.notFound("Route", routeId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route.get()));
    }

    /**
     * Update a route
     *
     * @param routeId  The route ID
     * @param resource The {@link UpdateRouteResource} instance
     * @return A {@link RouteResource} resource for the updated route
     */
    @PutMapping(value = "/{routeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update route", description = "Updates name, shift, vehicle, stops and schedule. A published route cannot change vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route updated",
                    content = @Content(schema = @Schema(implementation = RouteResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Route not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")
    })
    public ResponseEntity<?> updateRoute(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId,
            @Valid @RequestBody UpdateRouteResource resource) {
        var updateRouteCommand = UpdateRouteCommandFromResourceAssembler.toCommandFromResource(routeId, resource);
        return toRouteResponse(routeCommandService.handle(updateRouteCommand), HttpStatus.OK);
    }

    /**
     * Replace the weekly schedule of a route
     *
     * @param routeId  The route ID
     * @param resource The {@link UpdateRouteScheduleResource} instance
     * @return A {@link RouteResource} resource for the updated route
     */
    @PutMapping(value = "/{routeId}/schedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Configure weekly schedule", description = "Each service day must be unique; time format HH:mm.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule updated",
                    content = @Content(schema = @Schema(implementation = RouteResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Route not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")
    })
    public ResponseEntity<?> updateRouteSchedule(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId,
            @Valid @RequestBody UpdateRouteScheduleResource resource) {
        var command = UpdateRouteScheduleCommandFromResourceAssembler.toCommandFromResource(routeId, resource);
        return toRouteResponse(routeCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Change the status of a route. Only publication is supported.
     *
     * @param routeId  The route ID
     * @param resource The {@link UpdateRouteStatusResource} instance
     * @return A {@link RouteResource} resource for the published route
     */
    @PatchMapping(value = "/{routeId}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Publish route", description = "Requires stops, a confirmed schedule, a vehicle and at least one assigned student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route published",
                    content = @Content(schema = @Schema(implementation = RouteResource.class))),
            @ApiResponse(responseCode = "400", description = "Only publication is supported"),
            @ApiResponse(responseCode = "404", description = "Route not found"),
            @ApiResponse(responseCode = "409", description = "Route already published"),
            @ApiResponse(responseCode = "422", description = "Route incomplete")
    })
    public ResponseEntity<?> updateRouteStatus(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId,
            @Valid @RequestBody UpdateRouteStatusResource resource) {
        if (resource.status() != RouteStatus.PUBLISHED) {
            var error = ApplicationError.validationError("status", "Only publication is supported");
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return toRouteResponse(routeCommandService.handle(new PublishRouteCommand(routeId)), HttpStatus.OK);
    }

    /**
     * Delete a route
     *
     * @param routeId The route ID
     * @return A {@link MessageResource} confirming the deletion
     */
    @DeleteMapping("/{routeId}")
    @Operation(summary = "Delete route", description = "Deletes a route with its stops, schedule and assignments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route deleted"),
            @ApiResponse(responseCode = "404", description = "Route not found")
    })
    public ResponseEntity<?> deleteRoute(
            @PathVariable @Parameter(description = "Route unique identifier", example = "1", required = true) Long routeId) {
        var result = routeCommandService.handle(new DeleteRouteCommand(routeId))
                .map(id -> new MessageResource("Route with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }

    private ResponseEntity<?> toRouteResponse(Result<Long, ApplicationError> commandResult, HttpStatus successStatus) {
        var result = commandResult.flatMap(routeId -> routeQueryService.handle(new GetRouteByIdQuery(routeId))
                .<Result<Route, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Route", routeId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, RouteResourceFromEntityAssembler::toResourceFromEntity, successStatus);
    }
}
