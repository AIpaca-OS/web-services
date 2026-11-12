package com.aipaca.rumbo.platform.incidents.domain.model.queries;

/**
 * Get All Incidents By Trip ID Query
 *
 * @param tripId trip id
 */
public record GetAllIncidentsByTripIdQuery(Long tripId) {
    /**
     * Compact constructor for GetAllIncidentsByTripIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllIncidentsByTripIdQuery {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
    }
}
