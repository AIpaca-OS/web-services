package com.aipaca.rumbo.platform.trips.domain.model.commands;

/**
 * Start Trip Command
 *
 * @param tripId trip id
 */
public record StartTripCommand(Long tripId) {
    public StartTripCommand {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
    }
}
