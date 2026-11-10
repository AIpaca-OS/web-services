package com.aipaca.rumbo.platform.profiles.domain.model.commands;

public record EndServiceEnrollmentCommand(
        Long enrollmentId,
        Long requestedByProfileId
) {
    public EndServiceEnrollmentCommand {
        if (enrollmentId == null || enrollmentId < 1)
            throw new IllegalArgumentException("Invalid enrollment id");

        if (requestedByProfileId == null || requestedByProfileId < 1)
            throw new IllegalArgumentException("Invalid requester profile id");
    }
}