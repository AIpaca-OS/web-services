package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;

public record ResolveDataDeletionRequestCommand(
        Long deletionRequestId,
        DeletionRequestStatus status,
        String resolutionNote
) {
    public ResolveDataDeletionRequestCommand {
        if (deletionRequestId == null || deletionRequestId < 1)
            throw new IllegalArgumentException("Invalid deletion request id");

        if (status == null || status == DeletionRequestStatus.PENDING)
            throw new IllegalArgumentException(
                    "Resolution status must be APPROVED or REJECTED"
            );

        if (resolutionNote == null || resolutionNote.isBlank())
            throw new IllegalArgumentException("Resolution note is required");

        if (resolutionNote.length() > 500)
            throw new IllegalArgumentException(
                    "Resolution note cannot exceed 500 characters"
            );
    }
}