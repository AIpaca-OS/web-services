package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * Request Student Data Deletion Command
 *
 * @param studentId            student id
 * @param requestedByProfileId id of the tutor profile that requests it
 */
public record RequestStudentDataDeletionCommand(Long studentId, Long requestedByProfileId) {
    /**
     * Compact constructor for RequestStudentDataDeletionCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public RequestStudentDataDeletionCommand {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }
        if (requestedByProfileId == null || requestedByProfileId < 1) {
            throw new IllegalArgumentException("Invalid requester profile id");
        }
    }
}
