package com.aipaca.rumbo.platform.trips.interfaces.rest.resources;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteShift;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource for planning a trip from the snapshot of a published route.
 */
@Schema(
    name = "PlanTripRequest",
    description = "Snapshot of the published route and service date of the trip",
    example = "{\"routeId\": 1, \"driverProfileId\": 2, \"vehicleId\": 1, \"date\": \"2026-11-16\", \"shift\": \"MORNING\", \"passengers\": [{\"studentId\": 5, \"stopId\": 1, \"stopPosition\": 1, \"stopAddress\": \"Av. Primavera 123\", \"schoolId\": 1}]}"
)
public record PlanTripResource(
    @NotNull
    @Positive
    @Schema(description = "Id of the published route (Routes bounded context)", example = "1")
    Long routeId,

    @NotNull
    @Positive
    @Schema(description = "Id of the driver profile responsible for the route (Profiles bounded context)", example = "2")
    Long driverProfileId,

    @NotNull
    @Positive
    @Schema(description = "Id of the vehicle of the route (Vehicles bounded context)", example = "1")
    Long vehicleId,

    @NotNull
    @Schema(description = "Service date", example = "2026-11-16")
    LocalDate date,

    @NotNull
    @Schema(description = "Route shift", example = "MORNING")
    RouteShift shift,

    @NotEmpty
    @Schema(description = "Students assigned to the route with their stop")
    List<@Valid PlanTripPassengerResource> passengers
) {
}
