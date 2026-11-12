package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * Cancel Service Enrollment Command
 *
 * @param enrollmentId         enrollment id
 * @param requestedByProfileId id of the profile that cancels it
 */
public record CancelServiceEnrollmentCommand(Long enrollmentId, Long requestedByProfileId) {
    /**
     * Compact constructor for CancelServiceEnrollmentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public CancelServiceEnrollmentCommand {
        if (enrollmentId == null || enrollmentId < 1) {
            throw new IllegalArgumentException("Invalid enrollment id");
        }
        if (requestedByProfileId == null || requestedByProfileId < 1) {
            throw new IllegalArgumentException("Invalid requester id");
        }
    }
}
