package com.aipaca.rumbo.platform.profiles.domain.model.commands;

public record AcceptServiceEnrollmentCommand(
        Long enrollmentId,
        Long driverProfileId
) {
    public AcceptServiceEnrollmentCommand {
        if (enrollmentId == null || enrollmentId < 1)
            throw new IllegalArgumentException("Invalid enrollment id");

        if (driverProfileId == null || driverProfileId < 1)
            throw new IllegalArgumentException("Invalid driver profile id");
    }
}
