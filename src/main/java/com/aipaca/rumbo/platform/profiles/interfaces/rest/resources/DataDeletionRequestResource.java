package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resource for a data deletion request.
 */
@Schema(
    name = "DataDeletionRequestResponse",
    description = "Data deletion request information response"
)
public record DataDeletionRequestResource(
    @Schema(description = "Request unique identifier", example = "1")
    Long id,

    @Schema(description = "Student id", example = "1")
    Long studentId,

    @Schema(description = "Requester profile id", example = "1")
    Long requestedByProfileId,

    @Schema(description = "Request status", example = "PENDING")
    DeletionRequestStatus status,

    @Schema(description = "Request date")
    Instant requestedAt,

    @Schema(description = "Resolution date")
    Instant resolvedAt,

    @Schema(description = "Resolution note")
    String resolutionNote
) {
}
