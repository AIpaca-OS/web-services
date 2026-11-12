package com.aipaca.rumbo.incidents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for resolving an incident.
 */
@Schema(
    name = "ResolveIncidentRequest",
    description = "Request payload for resolving an open incident",
    example = "{\"resolution\": \"Se cambió de vehículo y el viaje continúa\"}"
)
public record ResolveIncidentResource(
    @NotBlank
    @Size(max = 500)
    @Schema(description = "Outcome of the incident", example = "Se cambió de vehículo y el viaje continúa", maxLength = 500)
    String resolution
) {
}
