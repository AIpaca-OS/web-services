package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * Request Service Enrollment Command
 *
 * @param studentId            student id
 * @param driverProfileId      driver profile id
 * @param requestedByProfileId id of the tutor profile that requests it
 */
public record RequestServiceEnrollmentCommand(
        Long studentId,
        Long driverProfileId,
        Long requestedByProfileId
) {
    /**
     * Compact constructor for RequestServiceEnrollmentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public RequestServiceEnrollmentCommand {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Invalid driver profile id");
        }
        if (requestedByProfileId == null || requestedByProfileId < 1) {
            throw new IllegalArgumentException("Invalid requester profile id");
        }
    }
}
