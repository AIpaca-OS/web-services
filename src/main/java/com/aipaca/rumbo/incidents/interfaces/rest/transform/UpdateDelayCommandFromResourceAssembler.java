package com.aipaca.rumbo.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateDelayCommand;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.UpdateDelayResource;

/**
 * Assembler to convert an UpdateDelayResource to an UpdateDelayCommand.
 */
public class UpdateDelayCommandFromResourceAssembler {
    /**
     * Converts an UpdateDelayResource to an UpdateDelayCommand.
     *
     * @param delayId  The id of the delay to update.
     * @param resource The {@link UpdateDelayResource} resource to convert.
     * @return The {@link UpdateDelayCommand} command.
     */
    public static UpdateDelayCommand toCommandFromResource(Long delayId, UpdateDelayResource resource) {
        return new UpdateDelayCommand(delayId, resource.reportedByProfileId(), resource.cause(),
                resource.estimatedMinutes());
    }
}
