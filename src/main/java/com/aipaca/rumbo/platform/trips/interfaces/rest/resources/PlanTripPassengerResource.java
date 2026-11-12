package com.aipaca.rumbo.platform.trips.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for a student assigned to a stop of the route a trip is planned from.
 */
@Schema(name = "PlanTripPassengerRequest", description = "Student assigned to a route stop")
public record PlanTripPassengerResource(
    @NotNull
    @Positive
    @Schema(description = "Student id (Profiles bounded context)", example = "5")
    Long studentId,

    @NotNull
    @Positive
    @Schema(description = "Route stop id (Routes bounded context)", example = "1")
    Long stopId,

    @NotNull
    @Positive
    @Schema(description = "Position of the stop in the route", example = "1")
    Integer stopPosition,

    @NotBlank
    @Schema(description = "Stop address", example = "Av. Primavera 123")
    String stopAddress,

    @Positive
    @Schema(description = "Id of the school served at the stop (Profiles bounded context)", example = "1", nullable = true)
    Long schoolId
) {
}
