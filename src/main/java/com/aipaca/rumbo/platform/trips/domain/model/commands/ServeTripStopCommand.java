package com.aipaca.rumbo.platform.trips.domain.model.commands;

/**
 * Serve Trip Stop Command
 *
 * @param tripId trip id
 * @param stopId id of the served stop
 */
public record ServeTripStopCommand(Long tripId, Long stopId) {
    public ServeTripStopCommand {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
        if (stopId == null || stopId < 1) {
            throw new IllegalArgumentException("Stop id cannot be null or less than 1");
        }
    }
}
