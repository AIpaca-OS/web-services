package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get Data Deletion Request By ID Query
 *
 * @param deletionRequestId deletion request id
 */
public record GetDataDeletionRequestByIdQuery(Long deletionRequestId) {
    /**
     * Compact constructor for GetDataDeletionRequestByIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetDataDeletionRequestByIdQuery {
        if (deletionRequestId == null || deletionRequestId < 1) {
            throw new IllegalArgumentException("Deletion request id cannot be null or less than 1");
        }
    }
}
