package com.aipaca.rumbo.platform.incidents.interfaces.rest;

import com.aipaca.rumbo.platform.incidents.application.commandservices.DelayCommandService;
import com.aipaca.rumbo.platform.incidents.application.queryservices.DelayQueryService;
import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetAllDelaysByTripIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetDelayByIdQuery;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.DelayResource;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.ReportDelayResource;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.UpdateDelayResource;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.transform.DelayResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.transform.ReportDelayCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.transform.UpdateDelayCommandFromResourceAssembler;
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
 * REST controller that exposes delay endpoints.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping(value = "/api/v1/delays", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Delays", description = "Delay management endpoints")
public class DelaysController {
    private final DelayCommandService delayCommandService;
    private final DelayQueryService delayQueryService;

    /**
     * Constructor
     *
     * @param delayCommandService The {@link DelayCommandService} instance
     * @param delayQueryService   The {@link DelayQueryService} instance
     */
    public DelaysController(DelayCommandService delayCommandService, DelayQueryService delayQueryService) {
        this.delayCommandService = delayCommandService;
        this.delayQueryService = delayQueryService;
    }

    /**
     * Report a delay
     *
     * @param resource The {@link ReportDelayResource} instance
     * @return A {@link DelayResource} resource for the reported delay
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Report a delay", description = "Registers a delay of a trip with its cause and estimated minutes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Delay reported",
                    content = @Content(schema = @Schema(implementation = DelayResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> reportDelay(@Valid @RequestBody ReportDelayResource resource) {
        var command = ReportDelayCommandFromResourceAssembler.toCommandFromResource(resource);
        return toDelayResponse(delayCommandService.handle(command));
    }

    /**
     * Get the delays of a trip
     *
     * @param tripId The trip ID
     * @return A list of {@link DelayResource} resources, most recent first
     */
    @GetMapping
    @Operation(summary = "Get delays by trip", description = "Lists every estimation of the delays of a trip, most recent first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delays found",
                    content = @Content(schema = @Schema(implementation = DelayResource.class)))
    })
    public ResponseEntity<List<DelayResource>> getDelaysByTripId(
            @RequestParam @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId) {
        var delays = delayQueryService.handle(new GetAllDelaysByTripIdQuery(tripId));
        return ResponseEntity.ok(delays.stream().map(DelayResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get a delay by ID
     *
     * @param delayId The delay ID
     * @return A {@link DelayResource} resource for the delay
     */
    @GetMapping("/{delayId}")
    @Operation(summary = "Get delay by ID", description = "Retrieves a delay by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delay found",
                    content = @Content(schema = @Schema(implementation = DelayResource.class))),
            @ApiResponse(responseCode = "404", description = "Delay not found")
    })
    public ResponseEntity<?> getDelayById(
            @PathVariable @Parameter(description = "Delay unique identifier", example = "1", required = true) Long delayId) {
        var delay = delayQueryService.handle(new GetDelayByIdQuery(delayId));
        if (delay.isEmpty()) {
            var error = ApplicationError.notFound("Delay", delayId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(DelayResourceFromEntityAssembler.toResourceFromEntity(delay.get()));
    }

    /**
     * Update the estimation of an active delay
     *
     * @param delayId  The ID of the delay to update
     * @param resource The {@link UpdateDelayResource} instance
     * @return A {@link DelayResource} resource for the new active delay
     */
    @PostMapping(value = "/{delayId}/updates", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update delay", description = "Records a new estimation that references the active delay; the previous delay is kept as SUPERSEDED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New estimation recorded",
                    content = @Content(schema = @Schema(implementation = DelayResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Delay not found"),
            @ApiResponse(responseCode = "422", description = "The delay was already updated")
    })
    public ResponseEntity<?> updateDelay(
            @PathVariable @Parameter(description = "Delay unique identifier", example = "1", required = true) Long delayId,
            @Valid @RequestBody UpdateDelayResource resource) {
        var command = UpdateDelayCommandFromResourceAssembler.toCommandFromResource(delayId, resource);
        return toDelayResponse(delayCommandService.handle(command));
    }

    private ResponseEntity<?> toDelayResponse(Result<Long, ApplicationError> commandResult) {
        var result = commandResult.flatMap(id -> delayQueryService.handle(new GetDelayByIdQuery(id))
                .<Result<Delay, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Delay", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, DelayResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
