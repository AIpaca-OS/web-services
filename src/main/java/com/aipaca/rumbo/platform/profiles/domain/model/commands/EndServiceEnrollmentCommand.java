package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * End Service Enrollment Command
 *
 * @param enrollmentId         enrollment id
 * @param requestedByProfileId id of the driver or authorized tutor that ends it
 */
public record EndServiceEnrollmentCommand(Long enrollmentId, Long requestedByProfileId) {
    /**
     * Compact constructor for EndServiceEnrollmentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public EndServiceEnrollmentCommand {
        if (enrollmentId == null || enrollmentId < 1) {
            throw new IllegalArgumentException("Invalid enrollment id");
        }
        if (requestedByProfileId == null || requestedByProfileId < 1) {
            throw new IllegalArgumentException("Invalid requester profile id");
        }
    }
}
