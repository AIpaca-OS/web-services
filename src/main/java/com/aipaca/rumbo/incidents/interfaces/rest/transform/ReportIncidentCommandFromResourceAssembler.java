package com.aipaca.rumbo.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportIncidentCommand;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.ReportIncidentResource;

/**
 * Assembler to convert a ReportIncidentResource to a ReportIncidentCommand.
 */
public class ReportIncidentCommandFromResourceAssembler {
    /**
     * Converts a ReportIncidentResource to a ReportIncidentCommand.
     *
     * @param resource The {@link ReportIncidentResource} resource to convert.
     * @return The {@link ReportIncidentCommand} command.
     */
    public static ReportIncidentCommand toCommandFromResource(ReportIncidentResource resource) {
        return new ReportIncidentCommand(resource.tripId(), resource.reportedByProfileId(), resource.studentId(),
                IncidentCategoryFromStringAssembler.toCategoryFromString(resource.category()), resource.description());
    }
}
