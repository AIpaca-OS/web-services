package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for requesting a service enrollment.
 */
@Schema(
    name = "RequestEnrollmentRequest",
    description = "Request payload for requesting a service enrollment",
    example = "{\"studentId\": 1, \"driverProfileId\": 3, \"requestedByProfileId\": 1}"
)
public record RequestEnrollmentResource(
    @NotNull
    @Positive
    @Schema(description = "Student id", example = "1")
    Long studentId,

    @NotNull
    @Positive
    @Schema(description = "Driver profile id", example = "3")
    Long driverProfileId,

    @NotNull
    @Positive
    @Schema(description = "Id of the authorized tutor that requests it", example = "1")
    Long requestedByProfileId
) {
}
