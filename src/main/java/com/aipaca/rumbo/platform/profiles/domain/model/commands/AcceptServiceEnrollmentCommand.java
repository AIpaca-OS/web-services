package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * Accept Service Enrollment Command
 *
 * @param enrollmentId    enrollment id
 * @param driverProfileId id of the driver profile that accepts it
 */
public record AcceptServiceEnrollmentCommand(Long enrollmentId, Long driverProfileId) {
    /**
     * Compact constructor for AcceptServiceEnrollmentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public AcceptServiceEnrollmentCommand {
        if (enrollmentId == null || enrollmentId < 1) {
            throw new IllegalArgumentException("Invalid enrollment id");
        }
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Invalid driver profile id");
        }
    }
}
