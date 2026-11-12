package com.aipaca.rumbo.platform.incidents.interfaces.rest.resources;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Resource for an incident.
 */
@Schema(name = "IncidentResponse", description = "Incident information response")
public record IncidentResource(
    @Schema(description = "Incident unique identifier", example = "1")
    Long id,

    @Schema(description = "Trip id", example = "1")
    Long tripId,

    @Schema(description = "Id of the driver profile that reported the incident", example = "2")
    Long reportedByProfileId,

    @Schema(description = "Id of the affected student", example = "1", nullable = true)
    Long studentId,

    @Schema(description = "Incident category", example = "MECHANICAL_FAILURE")
    IncidentCategory category,

    @Schema(description = "Incident description", example = "Falla en el motor, esperando auxilio mecánico")
    String description,

    @Schema(description = "Incident status", example = "OPEN")
    IncidentStatus status,

    @Schema(description = "Outcome of the incident", nullable = true)
    String resolution,

    @Schema(description = "Report date")
    Instant reportedAt,

    @Schema(description = "Resolution date", nullable = true)
    Instant resolvedAt,

    @Schema(description = "Tutor acknowledgements of the incident")
    List<IncidentAcknowledgementResource> acknowledgements
) {
}
