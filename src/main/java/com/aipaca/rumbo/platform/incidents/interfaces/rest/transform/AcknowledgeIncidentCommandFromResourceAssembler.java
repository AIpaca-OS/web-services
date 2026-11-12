package com.aipaca.rumbo.platform.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.AcknowledgeIncidentCommand;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.AcknowledgeIncidentResource;

/**
 * Assembler to convert an AcknowledgeIncidentResource to an AcknowledgeIncidentCommand.
 */
public class AcknowledgeIncidentCommandFromResourceAssembler {
    /**
     * Converts an AcknowledgeIncidentResource to an AcknowledgeIncidentCommand.
     *
     * @param incidentId The incident id.
     * @param resource   The {@link AcknowledgeIncidentResource} resource to convert.
     * @return The {@link AcknowledgeIncidentCommand} command.
     */
    public static AcknowledgeIncidentCommand toCommandFromResource(Long incidentId, AcknowledgeIncidentResource resource) {
        return new AcknowledgeIncidentCommand(incidentId, resource.tutorProfileId());
    }
}
