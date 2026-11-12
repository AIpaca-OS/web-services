package com.aipaca.rumbo.platform.trips.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for reporting the absence of a student on a trip.
 */
@Schema(name = "MarkStudentAbsenceRequest", description = "Student absent from a trip", example = "{\"studentId\": 1}")
public record MarkStudentAbsenceResource(
    @NotNull
    @Positive
    @Schema(description = "Student id", example = "1")
    Long studentId
) {
}
