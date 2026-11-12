package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for resolving a data deletion request.
 */
@Schema(
    name = "ResolveDeletionRequest",
    description = "Request payload for resolving a data deletion request",
    example = "{\"resolutionNote\": \"Request reviewed\"}"
)
public record ResolveDeletionResource(
    @NotBlank
    @Size(max = 500)
    @Schema(description = "Resolution note", example = "Request reviewed")
    String resolutionNote
) {
}
