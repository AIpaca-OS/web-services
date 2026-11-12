package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a profile.
 */
@Schema(
    name = "CreateProfileRequest",
    description = "Request payload for creating a profile",
    example = "{\"accountId\": 1, \"profileType\": \"TUTOR\", \"firstName\": \"Ana\", \"lastName\": \"Pérez\", \"phoneNumber\": \"987654321\"}"
)
public record CreateProfileResource(
    @NotNull
    @Positive
    @Schema(description = "Account id (IAM bounded context)", example = "1")
    Long accountId,

    @NotNull
    @Schema(description = "Profile type", example = "TUTOR")
    ProfileType profileType,

    @NotBlank
    @Size(max = 100)
    @Schema(description = "First name", example = "Ana")
    String firstName,

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Last name", example = "Pérez")
    String lastName,

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Contact phone number", example = "987654321")
    String phoneNumber
) {
}
