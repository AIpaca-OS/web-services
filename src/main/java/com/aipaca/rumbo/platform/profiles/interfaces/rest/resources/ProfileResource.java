package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for a profile.
 */
@Schema(
    name = "ProfileResponse",
    description = "Profile information response"
)
public record ProfileResource(
    @Schema(description = "Profile unique identifier", example = "1")
    Long id,

    @Schema(description = "Account id", example = "1")
    Long accountId,

    @Schema(description = "Profile type", example = "TUTOR")
    ProfileType profileType,

    @Schema(description = "First name", example = "Ana")
    String firstName,

    @Schema(description = "Last name", example = "Pérez")
    String lastName,

    @Schema(description = "Contact phone number", example = "987654321")
    String phoneNumber
) {
}
