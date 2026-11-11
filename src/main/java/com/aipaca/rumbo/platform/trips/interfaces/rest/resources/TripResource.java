package com.aipaca.rumbo.platform.trips.interfaces.rest.resources;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteShift;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.TripStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource for a trip.
 */
@Schema(name = "TripResponse", description = "Trip information response")
public record TripResource(
    @Schema(description = "Trip unique identifier", example = "1")
    Long id,

    @Schema(description = "Route id", example = "1")
    Long routeId,

    @Schema(description = "Id of the driver profile that runs the trip", example = "2")
    Long driverProfileId,

    @Schema(description = "Id of the vehicle that serves the trip", example = "1")
    Long vehicleId,

    @Schema(description = "Service date", example = "2026-11-16")
    LocalDate serviceDate,

    @Schema(description = "Route shift", example = "MORNING")
    RouteShift shift,

    @Schema(description = "Trip status", example = "SCHEDULED")
    TripStatus status,

    @Schema(description = "Cancellation reason", nullable = true)
    String cancelReason,

    @Schema(description = "Passengers ordered by stop position")
    List<TripPassengerResource> students
) {
}
