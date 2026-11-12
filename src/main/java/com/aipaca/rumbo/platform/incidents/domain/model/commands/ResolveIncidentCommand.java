package com.aipaca.rumbo.platform.incidents.domain.model.commands;

/**
 * Resolve Incident Command
 *
 * @param incidentId incident id
 * @param resolution resolution of the incident
 */
public record ResolveIncidentCommand(
        Long incidentId,
        String resolution
) {
    /**
     * Compact constructor for ResolveIncidentCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public ResolveIncidentCommand {
        if (incidentId == null || incidentId < 1) {
            throw new IllegalArgumentException("Incident id cannot be null or less than 1");
        }
    }
}
