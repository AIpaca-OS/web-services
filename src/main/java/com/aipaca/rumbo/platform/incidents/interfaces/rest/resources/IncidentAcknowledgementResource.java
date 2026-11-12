package com.aipaca.rumbo.platform.incidents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resource for an incident acknowledgement.
 */
@Schema(name = "IncidentAcknowledgementResponse", description = "Incident acknowledgement information response")
public record IncidentAcknowledgementResource(
    @Schema(description = "Acknowledgement unique identifier", example = "1")
    Long id,

    @Schema(description = "Id of the tutor profile that confirmed the reception", example = "1")
    Long tutorProfileId,

    @Schema(description = "Acknowledgement date")
    Instant acknowledgedAt
) {
}
