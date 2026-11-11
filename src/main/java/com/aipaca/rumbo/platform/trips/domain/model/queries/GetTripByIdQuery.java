package com.aipaca.rumbo.platform.trips.domain.model.queries;

/**
 * Get Trip By ID Query
 *
 * @param tripId trip id
 */
public record GetTripByIdQuery(Long tripId) {
    public GetTripByIdQuery {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
    }
}
