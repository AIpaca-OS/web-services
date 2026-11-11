package com.aipaca.rumbo.platform.trips.interfaces.rest;

import com.aipaca.rumbo.platform.trips.application.commandservices.TripCommandService;
import com.aipaca.rumbo.platform.trips.application.queryservices.TripQueryService;
import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.domain.model.commands.*;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetAllTripsByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetAllTripsByRouteIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetTripByIdQuery;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.CancelTripResource;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.MarkStudentAbsenceResource;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.PlanTripResource;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.TripResource;
import com.aipaca.rumbo.platform.trips.interfaces.rest.transform.CancelTripCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.trips.interfaces.rest.transform.MarkStudentAbsenceCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.trips.interfaces.rest.transform.PlanTripCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.trips.interfaces.rest.transform.TripResourceFromEntityAssembler;
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
 * REST controller that exposes trip execution endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/trips", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Trips", description = "Trip execution and monitoring endpoints")
public class TripsController {
    private final TripCommandService tripCommandService;
    private final TripQueryService tripQueryService;

    /**
     * Constructor
     *
     * @param tripCommandService The {@link TripCommandService} instance
     * @param tripQueryService   The {@link TripQueryService} instance
     */
    public TripsController(TripCommandService tripCommandService, TripQueryService tripQueryService) {
        this.tripCommandService = tripCommandService;
        this.tripQueryService = tripQueryService;
    }

    /**
     * Plan a trip from the snapshot of a published route
     *
     * @param resource The {@link PlanTripResource} instance
     * @return A {@link TripResource} resource for the planned trip
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Plan a trip", description = "Plans a trip for a service date from the snapshot of a published route.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trip planned",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Trip already scheduled for the route and date")
    })
    public ResponseEntity<?> planTrip(@Valid @RequestBody PlanTripResource resource) {
        var command = PlanTripCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = tripCommandService.handle(command)
                .flatMap(tripId -> tripQueryService.handle(new GetTripByIdQuery(tripId))
                        .<Result<Trip, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Trip", tripId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, TripResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Get the trips of a route or of a driver
     *
     * @param routeId         The route ID
     * @param driverProfileId The driver profile ID, used when no route ID is given
     * @return A list of {@link TripResource} resources, most recent service date first
     */
    @GetMapping
    @Operation(summary = "Get trips by route or driver", description = "Lists the trips of a route or of a driver, most recent service date first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trips found",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "400", description = "Neither routeId nor driverProfileId was given")
    })
    public ResponseEntity<?> getTrips(
            @RequestParam(required = false) @Parameter(description = "Route unique identifier", example = "1") Long routeId,
            @RequestParam(required = false) @Parameter(description = "Driver profile unique identifier", example = "2") Long driverProfileId) {
        if (routeId == null && driverProfileId == null) {
            var error = ApplicationError.validationError("Trip", "routeId or driverProfileId is required");
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        var trips = routeId != null
                ? tripQueryService.handle(new GetAllTripsByRouteIdQuery(routeId))
                : tripQueryService.handle(new GetAllTripsByDriverProfileIdQuery(driverProfileId));
        return ResponseEntity.ok(trips.stream().map(TripResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Get a trip by ID
     *
     * @param tripId The trip ID
     * @return A {@link TripResource} resource for the trip
     */
    @GetMapping("/{tripId}")
    @Operation(summary = "Get trip by ID", description = "Retrieves a trip and its passengers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip found",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found")
    })
    public ResponseEntity<?> getTripById(
            @PathVariable @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId) {
        var trip = tripQueryService.handle(new GetTripByIdQuery(tripId));
        if (trip.isEmpty()) {
            var error = ApplicationError.notFound("Trip", tripId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(TripResourceFromEntityAssembler.toResourceFromEntity(trip.get()));
    }

    @PatchMapping("/{tripId}/start")
    @Operation(summary = "Start trip", description = "Starts a scheduled trip.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip started",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "422", description = "Trip cannot be started")
    })
    public ResponseEntity<?> startTrip(
            @PathVariable @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId) {
        return toTripResponse(tripCommandService.handle(new StartTripCommand(tripId)));
    }

    @PostMapping(value = "/{tripId}/absences", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Report absence", description = "Marks a student as absent before the stop is served.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Absence recorded",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "422", description = "Absence not allowed")
    })
    public ResponseEntity<?> markStudentAbsence(
            @PathVariable @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId,
            @Valid @RequestBody MarkStudentAbsenceResource resource) {
        var command = MarkStudentAbsenceCommandFromResourceAssembler.toCommandFromResource(tripId, resource);
        return toTripResponse(tripCommandService.handle(command));
    }

    @PatchMapping("/{tripId}/stops/{stopId}/served")
    @Operation(summary = "Serve stop", description = "Marks a stop of a trip in progress as served.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stop served",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "422", description = "Stop cannot be served")
    })
    public ResponseEntity<?> serveTripStop(
            @PathVariable @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId,
            @PathVariable @Parameter(description = "Stop unique identifier", example = "1", required = true) Long stopId) {
        return toTripResponse(tripCommandService.handle(new ServeTripStopCommand(tripId, stopId)));
    }

    @PatchMapping("/{tripId}/passengers/{studentId}/arrived")
    @Operation(summary = "Confirm student arrival", description = "Confirms the arrival of a student after the assigned stop is served.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arrival confirmed",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "422", description = "Arrival cannot be confirmed")
    })
    public ResponseEntity<?> confirmStudentArrival(
            @PathVariable @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId,
            @PathVariable @Parameter(description = "Student unique identifier", example = "1", required = true) Long studentId) {
        return toTripResponse(tripCommandService.handle(new ConfirmStudentArrivalCommand(tripId, studentId)));
    }

    @PatchMapping(value = "/{tripId}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cancel trip", description = "Cancels a trip that is neither completed nor cancelled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip cancelled",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "422", description = "Trip cannot be cancelled")
    })
    public ResponseEntity<?> cancelTrip(
            @PathVariable @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId,
            @Valid @RequestBody CancelTripResource resource) {
        var command = CancelTripCommandFromResourceAssembler.toCommandFromResource(tripId, resource);
        return toTripResponse(tripCommandService.handle(command));
    }

    @PatchMapping("/{tripId}/complete")
    @Operation(summary = "Complete trip", description = "Completes a trip in progress.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip completed",
                    content = @Content(schema = @Schema(implementation = TripResource.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "422", description = "Trip is not in progress")
    })
    public ResponseEntity<?> completeTrip(
            @PathVariable @Parameter(description = "Trip unique identifier", example = "1", required = true) Long tripId) {
        return toTripResponse(tripCommandService.handle(new CompleteTripCommand(tripId)));
    }

    private ResponseEntity<?> toTripResponse(Result<Long, ApplicationError> commandResult) {
        var result = commandResult.flatMap(tripId -> tripQueryService.handle(new GetTripByIdQuery(tripId))
                .<Result<Trip, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Trip", tripId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, TripResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
