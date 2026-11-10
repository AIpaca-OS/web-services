package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetAllDataDeletionRequestsByRequestedByProfileIdQuery(
        Long requestedByProfileId
) {
    public GetAllDataDeletionRequestsByRequestedByProfileIdQuery {
        if (requestedByProfileId == null || requestedByProfileId < 1) {
            throw new IllegalArgumentException(
                    "Requester profile id cannot be null or less than 1"
            );
        }
    }
}
