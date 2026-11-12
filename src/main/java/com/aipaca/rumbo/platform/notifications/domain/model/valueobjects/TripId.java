package com.aipaca.rumbo.platform.notifications.domain.model.valueobjects;

/**
 * Value object representing the trip id.
 *
 * <p>Links a notification to a trip of the Trips bounded context.</p>
 *
 * @param tripId The trip id. It cannot be null or less than 1.
 */
public record TripId(Long tripId) {
    public TripId {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
    }
}
