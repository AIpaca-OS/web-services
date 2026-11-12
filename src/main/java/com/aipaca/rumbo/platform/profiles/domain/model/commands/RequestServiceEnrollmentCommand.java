package com.aipaca.rumbo.platform.profiles.domain.model.commands;

public record RequestServiceEnrollmentCommand(
        Long studentId,
        Long driverProfileId,
        Long requestedByProfileId
) {
    public RequestServiceEnrollmentCommand {
        if (studentId == null || studentId < 1)
            throw new IllegalArgumentException("Invalid student id");

        if (driverProfileId == null || driverProfileId < 1)
            throw new IllegalArgumentException("Invalid driver profile id");

        if (requestedByProfileId == null || requestedByProfileId < 1)
            throw new IllegalArgumentException("Invalid requester profile id");
    }
}
