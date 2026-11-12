package com.aipaca.rumbo.incidents.domain.model.valueobjects;

/**
 * Value object representing the trip id.
 *
 * <p>Links an incident or a delay to a trip of the Trips bounded context.</p>
 *
 * @param tripId The trip id. It cannot be null or less than 1.
 */
public record TripId(Long tripId) {
    /**
     * Compact constructor for TripId.
     *
     * @throws IllegalArgumentException if the trip id is null or less than 1.
     */
    public TripId {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
    }
}
