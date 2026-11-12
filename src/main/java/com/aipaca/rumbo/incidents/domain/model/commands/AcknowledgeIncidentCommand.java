package com.aipaca.rumbo.incidents.domain.model.commands;

/**
 * Acknowledge Incident Command
 *
 * @param incidentId     incident id
 * @param tutorProfileId id of the tutor profile that confirms the reception
 */
public record AcknowledgeIncidentCommand(
        Long incidentId,
        Long tutorProfileId
) {
    /**
     * Compact constructor for AcknowledgeIncidentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public AcknowledgeIncidentCommand {
        if (incidentId == null || incidentId < 1) {
            throw new IllegalArgumentException("Incident id cannot be null or less than 1");
        }
        if (tutorProfileId == null || tutorProfileId < 1) {
            throw new IllegalArgumentException("Tutor profile id cannot be null or less than 1");
        }
    }
}
