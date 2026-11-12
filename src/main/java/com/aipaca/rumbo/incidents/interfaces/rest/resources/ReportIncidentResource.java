package com.aipaca.rumbo.incidents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Resource for reporting an incident.
 */
@Schema(
    name = "ReportIncidentRequest",
    description = "Request payload for reporting an incident during a trip",
    example = "{\"tripId\": 1, \"reportedByProfileId\": 2, \"studentId\": null, \"category\": \"MECHANICAL_FAILURE\", \"description\": \"Falla en el motor, esperando auxilio mecánico\"}"
)
public record ReportIncidentResource(
    @NotNull
    @Positive
    @Schema(description = "Trip id", example = "1")
    Long tripId,

    @NotNull
    @Positive
    @Schema(description = "Id of the driver profile that reports the incident", example = "2")
    Long reportedByProfileId,

    @Positive
    @Schema(description = "Id of the affected student; null when the incident affects the whole trip", example = "1", nullable = true)
    Long studentId,

    @NotBlank
    @Schema(description = "Incident category: MECHANICAL_FAILURE, TRAFFIC_ACCIDENT, ROAD_BLOCKED, STUDENT_HEALTH or OTHER", example = "MECHANICAL_FAILURE")
    String category,

    @NotBlank
    @Size(max = 500)
    @Schema(description = "Incident description", example = "Falla en el motor, esperando auxilio mecánico", maxLength = 500)
    String description
) {
}
