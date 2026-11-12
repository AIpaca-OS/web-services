package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for updating the contact of a profile.
 */
@Schema(
    name = "UpdateProfileContactRequest",
    description = "Request payload for updating the contact of a profile",
    example = "{\"phoneNumber\": \"987654321\"}"
)
public record UpdateProfileContactResource(
    @NotBlank
    @Size(max = 20)
    @Schema(description = "Contact phone number", example = "987654321")
    String phoneNumber
) {
}
