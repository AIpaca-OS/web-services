package com.aipaca.rumbo.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateIncidentCommand;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.UpdateIncidentResource;

/**
 * Assembler to convert an UpdateIncidentResource to an UpdateIncidentCommand.
 */
public class UpdateIncidentCommandFromResourceAssembler {
    /**
     * Converts an UpdateIncidentResource to an UpdateIncidentCommand.
     *
     * @param incidentId The incident id.
     * @param resource   The {@link UpdateIncidentResource} resource to convert.
     * @return The {@link UpdateIncidentCommand} command.
     */
    public static UpdateIncidentCommand toCommandFromResource(Long incidentId, UpdateIncidentResource resource) {
        return new UpdateIncidentCommand(incidentId,
                IncidentCategoryFromStringAssembler.toCategoryFromString(resource.category()), resource.description());
    }
}
