package com.aipaca.rumbo.platform.trips.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for a trip passenger.
 */
@Schema(name = "TripPassengerResponse", description = "Student snapshot on a trip")
public record TripPassengerResource(
    @Schema(description = "Student id", example = "1")
    Long studentId,

    @Schema(description = "Stop id", example = "1")
    Long stopId,

    @Schema(description = "Stop position", example = "1")
    int stopPosition,

    @Schema(description = "Stop address", example = "Av. Primavera 123")
    String stopAddress,

    @Schema(description = "Whether the student was reported absent", example = "false")
    boolean absent,

    @Schema(description = "Whether the stop was served", example = "false")
    boolean stopServed,

    @Schema(description = "Whether the driver confirmed the arrival", example = "false")
    boolean arrived
) {
}
