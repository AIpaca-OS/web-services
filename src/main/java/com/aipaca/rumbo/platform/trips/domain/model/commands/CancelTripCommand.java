package com.aipaca.rumbo.platform.trips.domain.model.commands;

/**
 * Cancel Trip Command
 *
 * @param tripId trip id
 * @param reason cancellation reason
 */
public record CancelTripCommand(Long tripId, String reason) {
    public CancelTripCommand {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason cannot be null or blank");
        }
    }
}
