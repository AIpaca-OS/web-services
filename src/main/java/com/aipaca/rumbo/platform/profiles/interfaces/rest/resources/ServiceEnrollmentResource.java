package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ServiceEnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resource for a service enrollment.
 */
@Schema(
    name = "ServiceEnrollmentResponse",
    description = "Service enrollment information response"
)
public record ServiceEnrollmentResource(
    @Schema(description = "Enrollment unique identifier", example = "1")
    Long id,

    @Schema(description = "Student id", example = "1")
    Long studentId,

    @Schema(description = "Driver profile id", example = "3")
    Long driverProfileId,

    @Schema(description = "Requester profile id", example = "1")
    Long requestedByProfileId,

    @Schema(description = "Enrollment status", example = "PENDING")
    ServiceEnrollmentStatus status,

    @Schema(description = "Request date")
    Instant requestedAt,

    @Schema(description = "Acceptance date")
    Instant acceptedAt,

    @Schema(description = "Cancellation date")
    Instant cancelledAt,

    @Schema(description = "End date")
    Instant endedAt
) {
}
