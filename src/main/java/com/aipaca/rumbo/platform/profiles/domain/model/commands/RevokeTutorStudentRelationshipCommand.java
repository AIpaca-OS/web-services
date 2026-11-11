package com.aipaca.rumbo.platform.profiles.domain.model.commands;

public record RevokeTutorStudentRelationshipCommand(
        Long relationshipId,
        Long requestedByProfileId
) {
    public RevokeTutorStudentRelationshipCommand {
        if (relationshipId == null || relationshipId < 1)
            throw new IllegalArgumentException("Invalid relationship id");

        if (requestedByProfileId == null || requestedByProfileId < 1)
            throw new IllegalArgumentException("Invalid requester profile id");
    }
}