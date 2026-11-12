package com.aipaca.rumbo.incidents.domain.model.commands;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;

/**
 * Report Incident Command
 *
 * @param tripId              trip id
 * @param reportedByProfileId id of the driver profile that reports it
 * @param studentId           id of the affected student, or null when the incident affects the whole trip
 * @param category            incident category
 * @param description         incident description
 */
public record ReportIncidentCommand(
        Long tripId,
        Long reportedByProfileId,
        Long studentId,
        IncidentCategory category,
        String description
) {
    /**
     * Compact constructor for ReportIncidentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public ReportIncidentCommand {
        if (tripId == null || tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be null or less than 1");
        }
        if (reportedByProfileId == null || reportedByProfileId < 1) {
            throw new IllegalArgumentException("Reporter profile id cannot be null or less than 1");
        }
        if (studentId != null && studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be less than 1");
        }
        if (category == null) {
            throw new IllegalArgumentException("Incident category cannot be null");
        }
    }
}
