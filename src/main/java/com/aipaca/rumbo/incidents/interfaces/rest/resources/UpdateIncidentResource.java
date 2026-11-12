package com.aipaca.rumbo.incidents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for updating an open incident.
 */
@Schema(
    name = "UpdateIncidentRequest",
    description = "Request payload for updating an open incident",
    example = "{\"category\": \"MECHANICAL_FAILURE\", \"description\": \"El auxilio mecánico llega en 20 minutos\"}"
)
public record UpdateIncidentResource(
    @NotBlank
    @Schema(description = "Incident category: MECHANICAL_FAILURE, TRAFFIC_ACCIDENT, ROAD_BLOCKED, STUDENT_HEALTH or OTHER", example = "MECHANICAL_FAILURE")
    String category,

    @NotBlank
    @Size(max = 500)
    @Schema(description = "Incident description", example = "El auxilio mecánico llega en 20 minutos", maxLength = 500)
    String description
) {
}
