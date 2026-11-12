package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resource for a tutor authorization of a student.
 */
@Schema(
    name = "TutorStudentRelationshipResponse",
    description = "Tutor authorization of a student information response"
)
public record TutorStudentRelationshipResource(
    @Schema(description = "Relationship unique identifier", example = "1")
    Long id,

    @Schema(description = "Authorized tutor profile id", example = "2")
    Long tutorProfileId,

    @Schema(description = "Profile id of the tutor that granted it", example = "1")
    Long authorizedByProfileId,

    @Schema(description = "Relationship between the tutor and the student", example = "FATHER")
    RelationshipType relationshipType,

    @Schema(description = "Authorization status", example = "AUTHORIZED")
    AuthorizationStatus authorizationStatus,

    @Schema(description = "Authorization date")
    Instant authorizedAt,

    @Schema(description = "Revocation date")
    Instant revokedAt
) {
}
