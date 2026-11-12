package com.aipaca.rumbo.platform.incidents.domain.model.commands;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;

/**
 * Update Incident Command
 *
 * @param incidentId  incident id
 * @param category    incident category
 * @param description incident description
 */
public record UpdateIncidentCommand(
        Long incidentId,
        IncidentCategory category,
        String description
) {
    /**
     * Compact constructor for UpdateIncidentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public UpdateIncidentCommand {
        if (incidentId == null || incidentId < 1) {
            throw new IllegalArgumentException("Incident id cannot be null or less than 1");
        }
        if (category == null) {
            throw new IllegalArgumentException("Incident category cannot be null");
        }
    }
}
