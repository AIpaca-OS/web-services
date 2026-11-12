package com.aipaca.rumbo.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportDelayCommand;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.ReportDelayResource;

/**
 * Assembler to convert a ReportDelayResource to a ReportDelayCommand.
 */
public class ReportDelayCommandFromResourceAssembler {
    /**
     * Converts a ReportDelayResource to a ReportDelayCommand.
     *
     * @param resource The {@link ReportDelayResource} resource to convert.
     * @return The {@link ReportDelayCommand} command.
     */
    public static ReportDelayCommand toCommandFromResource(ReportDelayResource resource) {
        return new ReportDelayCommand(resource.tripId(), resource.reportedByProfileId(), resource.cause(),
                resource.estimatedMinutes());
    }
}
