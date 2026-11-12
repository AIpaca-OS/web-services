package com.aipaca.rumbo.incidents.domain.model.commands;

/**
 * Update Delay Command. Records a new estimation that replaces an active delay.
 *
 * @param delayId             id of the delay to update
 * @param reportedByProfileId id of the driver profile that reports the update
 * @param cause               delay cause
 * @param estimatedMinutes    new estimated delay in minutes
 */
public record UpdateDelayCommand(
        Long delayId,
        Long reportedByProfileId,
        String cause,
        Integer estimatedMinutes
) {
    /**
     * Compact constructor for UpdateDelayCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public UpdateDelayCommand {
        if (delayId == null || delayId < 1) {
            throw new IllegalArgumentException("Delay id cannot be null or less than 1");
        }
        if (reportedByProfileId == null || reportedByProfileId < 1) {
            throw new IllegalArgumentException("Reporter profile id cannot be null or less than 1");
        }
    }
}
