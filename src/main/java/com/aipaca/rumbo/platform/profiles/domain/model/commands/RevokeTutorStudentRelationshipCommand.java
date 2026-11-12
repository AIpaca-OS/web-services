package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * Revoke Tutor Student Relationship Command
 *
 * @param studentId            student id
 * @param tutorProfileId       id of the tutor profile to revoke
 * @param requestedByProfileId id of an authorized tutor that revokes the authorization
 */
public record RevokeTutorStudentRelationshipCommand(
        Long studentId,
        Long tutorProfileId,
        Long requestedByProfileId
) {
    /**
     * Compact constructor for RevokeTutorStudentRelationshipCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public RevokeTutorStudentRelationshipCommand {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }
        if (tutorProfileId == null || tutorProfileId < 1) {
            throw new IllegalArgumentException("Invalid tutor profile id");
        }
        if (requestedByProfileId == null || requestedByProfileId < 1) {
            throw new IllegalArgumentException("Invalid requester profile id");
        }
    }
}
