package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/** Revoke through Student aggregate, identified by student and tutor IDs. */
public record RevokeTutorStudentRelationshipCommand(
        Long studentId, Long tutorProfileId, Long requestedByProfileId
) {
    public RevokeTutorStudentRelationshipCommand {
        if (studentId == null || studentId < 1) throw new IllegalArgumentException("Invalid student id");
        if (tutorProfileId == null || tutorProfileId < 1) throw new IllegalArgumentException("Invalid tutor profile id");
        if (requestedByProfileId == null || requestedByProfileId < 1) throw new IllegalArgumentException("Invalid requester profile id");
    }
}
