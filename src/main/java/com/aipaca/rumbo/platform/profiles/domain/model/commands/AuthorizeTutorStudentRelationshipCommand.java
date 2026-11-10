package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;

public record AuthorizeTutorStudentRelationshipCommand(
        Long studentId,
        Long tutorProfileId,
        Long authorizedByProfileId,
        RelationshipType relationshipType
) {
    public AuthorizeTutorStudentRelationshipCommand {
        if (studentId == null || studentId < 1)
            throw new IllegalArgumentException("Invalid student id");

        if (tutorProfileId == null || tutorProfileId < 1)
            throw new IllegalArgumentException("Invalid tutor profile id");

        if (authorizedByProfileId == null || authorizedByProfileId < 1)
            throw new IllegalArgumentException("Invalid authorizer profile id");

        if (relationshipType == null)
            throw new IllegalArgumentException("Relationship type is required");
    }
}