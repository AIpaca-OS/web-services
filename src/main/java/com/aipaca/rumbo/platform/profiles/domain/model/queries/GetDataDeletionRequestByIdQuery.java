package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetDataDeletionRequestByIdQuery(
        Long deletionRequestId
) {
    public GetDataDeletionRequestByIdQuery {
        if (deletionRequestId == null || deletionRequestId < 1) {
            throw new IllegalArgumentException(
                    "Deletion request id cannot be null or less than 1"
            );
        }
    }
}
