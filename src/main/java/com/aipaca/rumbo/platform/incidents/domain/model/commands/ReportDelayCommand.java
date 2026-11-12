package com.aipaca.rumbo.platform.incidents.domain.model.commands;

/**
 * Report Delay Command
 *
 * @param tripId              trip id
 * @param reportedByProfileId id of the driver profile that reports it
 * @param cause               delay cause
 * @param estimatedMinutes    estimated delay in minutes
 */
public record ReportDelayCommand(
        Long tripId,
        Long reportedByProfileId,
        String cause,
        Integer estimatedMinutes
) {
    /**
     * Compact constructor for ReportDelayCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public ReportDelayCommand {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
        if (reportedByProfileId == null || reportedByProfileId < 1) {
            throw new IllegalArgumentException("Reporter profile id cannot be null or less than 1");
        }
    }
}
