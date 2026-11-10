package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * Requests termination by a currently authorized tutor or the enrolled driver.
 * requestedByProfileId identifies the actor, not necessarily the original requester.
 * Application services must verify this identity and permission before ending the enrollment.
 */
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
