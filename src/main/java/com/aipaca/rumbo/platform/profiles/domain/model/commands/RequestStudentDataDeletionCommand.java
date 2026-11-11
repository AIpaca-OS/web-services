package com.aipaca.rumbo.platform.profiles.domain.model.commands;

public record RequestStudentDataDeletionCommand(
        Long studentId,
        Long requestedByProfileId
) {
    public RequestStudentDataDeletionCommand {
        if (studentId == null || studentId < 1)
            throw new IllegalArgumentException("Invalid student id");

        if (requestedByProfileId == null || requestedByProfileId < 1)
            throw new IllegalArgumentException("Invalid requester profile id");
    }
}
