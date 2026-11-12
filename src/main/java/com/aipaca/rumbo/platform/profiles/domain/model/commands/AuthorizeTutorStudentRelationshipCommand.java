package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;

/**
 * Authorize Tutor Student Relationship Command
 *
 * @param studentId             student id
 * @param tutorProfileId        id of the tutor profile to authorize
 * @param authorizedByProfileId id of an already authorized tutor that grants the authorization
 * @param relationshipType      relationship between tutor and student
 */
public record AuthorizeTutorStudentRelationshipCommand(
        Long studentId,
        Long tutorProfileId,
        Long authorizedByProfileId,
        RelationshipType relationshipType
) {
    /**
     * Compact constructor for AuthorizeTutorStudentRelationshipCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public AuthorizeTutorStudentRelationshipCommand {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }
        if (tutorProfileId == null || tutorProfileId < 1) {
            throw new IllegalArgumentException("Invalid tutor profile id");
        }
        if (authorizedByProfileId == null || authorizedByProfileId < 1) {
            throw new IllegalArgumentException("Invalid authorizer profile id");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("Relationship type is required");
        }
    }
}
