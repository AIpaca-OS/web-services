package com.aipaca.rumbo.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ResolveIncidentCommand;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.ResolveIncidentResource;

/**
 * Assembler to convert a ResolveIncidentResource to a ResolveIncidentCommand.
 */
public class ResolveIncidentCommandFromResourceAssembler {
    /**
     * Converts a ResolveIncidentResource to a ResolveIncidentCommand.
     *
     * @param incidentId The incident id.
     * @param resource   The {@link ResolveIncidentResource} resource to convert.
     * @return The {@link ResolveIncidentCommand} command.
     */
    public static ResolveIncidentCommand toCommandFromResource(Long incidentId, ResolveIncidentResource resource) {
        return new ResolveIncidentCommand(incidentId, resource.resolution());
    }
}
