package com.aipaca.rumbo.platform.incidents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for acknowledging an incident.
 */
@Schema(
    name = "AcknowledgeIncidentRequest",
    description = "Request payload for a tutor confirming the reception of an incident",
    example = "{\"tutorProfileId\": 1}"
)
public record AcknowledgeIncidentResource(
    @NotNull
    @Positive
    @Schema(description = "Id of the tutor profile that confirms the reception", example = "1")
    Long tutorProfileId
) {
}
