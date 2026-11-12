package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for authorizing a tutor of a student.
 */
@Schema(
    name = "AuthorizeTutorRequest",
    description = "Request payload for authorizing a tutor of a student",
    example = "{\"tutorProfileId\": 2, \"authorizedByProfileId\": 1, \"relationshipType\": \"FATHER\"}"
)
public record AuthorizeTutorResource(
    @NotNull
    @Positive
    @Schema(description = "Id of the tutor profile to authorize", example = "2")
    Long tutorProfileId,

    @NotNull
    @Positive
    @Schema(description = "Id of an already authorized tutor that grants the authorization", example = "1")
    Long authorizedByProfileId,

    @NotNull
    @Schema(description = "Relationship between the tutor and the student", example = "FATHER")
    RelationshipType relationshipType
) {
}
