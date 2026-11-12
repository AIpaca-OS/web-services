package com.aipaca.rumbo.incidents.interfaces.rest;

import com.aipaca.rumbo.platform.incidents.application.commandservices.IncidentCommandService;
import com.aipaca.rumbo.platform.incidents.application.queryservices.IncidentQueryService;
import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetAllIncidentsByTripIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetIncidentByIdQuery;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.*;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.transform.*;
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
 * REST controller that exposes incident endpoints.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/incidents", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Incidents", description = "Incident management endpoints")
public class IncidentsController {
    private final IncidentCommandService incidentCommandService;
    private final IncidentQueryService incidentQueryService;

    /**
     * Constructor
     *
     * @param incidentCommandService The {@link IncidentCommandService} instance
     * @param incidentQueryService   The {@link IncidentQueryService} instance
     */
    public IncidentsController(IncidentCommandService incidentCommandService,
                               IncidentQueryService incidentQueryService) {
        this.incidentCommandService = incidentCommandService;
        this.incidentQueryService = incidentQueryService;
    }

    /**
     * Report an incident
     *
     * @param resource The {@link ReportIncidentResource} instance
     * @return A {@link IncidentResource} resource for the reported incident
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Report an incident", description = "Registers an incident of a trip with its category and description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incident reported",
                    content = @Content(schema = @Schema(implementation = IncidentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> reportIncident(@Valid @RequestBody ReportIncidentResource resource) {
        var command = ReportIncidentCommandFromResourceAssembler.toCommandFromResource(resource);
        return toIncidentResponse(incidentCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Get the incidents of a trip
     *
     * @param tripId The trip ID
     * @return A list of {@link IncidentResource} resources, most recent first
     */
    @GetMapping
    @Operation(summary = "Get incidents by trip", description = "Lists the incidents of a trip, most recent first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidents found",
                    content = @Content(schema = @Schema(implementation = IncidentResource.class)))
    })
    public ResponseEntity<List<IncidentResource>> getIncidentsByTripId(
            @RequestParam @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId) {
        var incidents = incidentQueryService.handle(new GetAllIncidentsByTripIdQuery(tripId));
        return ResponseEntity.ok(incidents.stream().map(IncidentResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get an incident by ID
     *
     * @param incidentId The incident ID
     * @return A {@link IncidentResource} resource for the incident
     */
    @GetMapping("/{incidentId}")
    @Operation(summary = "Get incident by ID", description = "Retrieves an incident and its acknowledgements.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident found",
                    content = @Content(schema = @Schema(implementation = IncidentResource.class))),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    public ResponseEntity<?> getIncidentById(
            @PathVariable @Parameter(description = "Incident unique identifier", example = "1", required = true)
            Long incidentId) {
        var incident = incidentQueryService.handle(new GetIncidentByIdQuery(incidentId));
        if (incident.isEmpty()) {
            var error = ApplicationError.notFound("Incident", incidentId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(IncidentResourceFromEntityAssembler.toResourceFromEntity(incident.get()));
    }

    /**
     * Update an open incident
     *
     * @param incidentId The incident ID
     * @param resource   The {@link UpdateIncidentResource} instance
     * @return A {@link IncidentResource} resource for the updated incident
     */
    @PutMapping(value = "/{incidentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update incident", description = "Updates the category and the description of an open incident.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident updated",
                    content = @Content(schema = @Schema(implementation = IncidentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Incident not found"),
            @ApiResponse(responseCode = "422", description = "Incident already resolved")
    })
    public ResponseEntity<?> updateIncident(
            @PathVariable @Parameter(description = "Incident unique identifier", example = "1", required = true)
            Long incidentId,
            @Valid @RequestBody UpdateIncidentResource resource) {
        var command = UpdateIncidentCommandFromResourceAssembler.toCommandFromResource(incidentId, resource);
        return toIncidentResponse(incidentCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Resolve an open incident
     *
     * @param incidentId The incident ID
     * @param resource   The {@link ResolveIncidentResource} instance
     * @return A {@link IncidentResource} resource for the resolved incident
     */
    @PatchMapping(value = "/{incidentId}/resolve", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Resolve incident", description = "Records the outcome of an open incident. A resolved incident keeps its original resolution.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident resolved",
                    content = @Content(schema = @Schema(implementation = IncidentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Incident not found"),
            @ApiResponse(responseCode = "422", description = "Incident already resolved")
    })
    public ResponseEntity<?> resolveIncident(
            @PathVariable @Parameter(description = "Incident unique identifier", example = "1", required = true)
            Long incidentId,
            @Valid @RequestBody ResolveIncidentResource resource) {
        var command = ResolveIncidentCommandFromResourceAssembler.toCommandFromResource(incidentId, resource);
        return toIncidentResponse(incidentCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Acknowledge an incident
     *
     * @param incidentId The incident ID
     * @param resource   The {@link AcknowledgeIncidentResource} instance
     * @return A {@link IncidentResource} resource with the new acknowledgement
     */
    @PostMapping(value = "/{incidentId}/acknowledgements", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Acknowledge incident", description = "Records that a tutor took notice of an incident. Each tutor acknowledges it only once.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Acknowledgement recorded",
                    content = @Content(schema = @Schema(implementation = IncidentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Incident not found"),
            @ApiResponse(responseCode = "422", description = "The tutor already acknowledged the incident")
    })
    public ResponseEntity<?> acknowledgeIncident(
            @PathVariable @Parameter(description = "Incident unique identifier", example = "1", required = true)
            Long incidentId,
            @Valid @RequestBody AcknowledgeIncidentResource resource) {
        var command = AcknowledgeIncidentCommandFromResourceAssembler.toCommandFromResource(incidentId, resource);
        return toIncidentResponse(incidentCommandService.handle(command), HttpStatus.CREATED);
    }

    private ResponseEntity<?> toIncidentResponse(Result<Long, ApplicationError> commandResult, HttpStatus status) {
        var result = commandResult.flatMap(id -> incidentQueryService.handle(new GetIncidentByIdQuery(id))
                .<Result<Incident, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Incident", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, IncidentResourceFromEntityAssembler::toResourceFromEntity, status);
    }
}
