package com.aipaca.rumbo.platform.trips.domain.model.commands;

/**
 * Complete Trip Command
 *
 * @param tripId trip id
 */
public record CompleteTripCommand(Long tripId) {
    public CompleteTripCommand {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
    }
}
