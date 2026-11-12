package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get All Data Deletion Requests By Requested By Profile ID Query
 *
 * @param requestedByProfileId requester profile id
 */
public record GetAllDataDeletionRequestsByRequestedByProfileIdQuery(Long requestedByProfileId) {
    /**
     * Compact constructor for GetAllDataDeletionRequestsByRequestedByProfileIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllDataDeletionRequestsByRequestedByProfileIdQuery {
        if (requestedByProfileId == null || requestedByProfileId < 1) {
            throw new IllegalArgumentException("Requester profile id cannot be null or less than 1");
        }
    }
}
