package com.aipaca.rumbo.platform.incidents.domain.model.queries;

/**
 * Get All Delays By Trip ID Query
 *
 * @param tripId trip id
 */
public record GetAllDelaysByTripIdQuery(Long tripId) {
    /**
     * Compact constructor for GetAllDelaysByTripIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllDelaysByTripIdQuery {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
    }
}
