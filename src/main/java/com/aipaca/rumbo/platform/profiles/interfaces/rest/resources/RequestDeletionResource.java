package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for requesting the deletion of the data of a student.
 */
@Schema(
    name = "RequestDeletionRequest",
    description = "Request payload for requesting the deletion of the data of a student",
    example = "{\"studentId\": 1, \"requestedByProfileId\": 1}"
)
public record RequestDeletionResource(
    @NotNull
    @Positive
    @Schema(description = "Student id", example = "1")
    Long studentId,

    @NotNull
    @Positive
    @Schema(description = "Id of the authorized tutor that requests it", example = "1")
    Long requestedByProfileId
) {
}
