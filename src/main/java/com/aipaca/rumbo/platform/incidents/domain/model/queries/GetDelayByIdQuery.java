package com.aipaca.rumbo.platform.incidents.domain.model.queries;

/**
 * Get Delay By ID Query
 *
 * @param delayId delay id
 */
public record GetDelayByIdQuery(Long delayId) {
    /**
     * Compact constructor for GetDelayByIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetDelayByIdQuery {
        if (delayId == null || delayId < 1) {
            throw new IllegalArgumentException("Delay id cannot be null or less than 1");
        }
    }
}
