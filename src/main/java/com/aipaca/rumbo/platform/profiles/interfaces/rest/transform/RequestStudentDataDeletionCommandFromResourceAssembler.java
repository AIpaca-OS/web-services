package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.RequestDeletionResource;

/**
 * Assembler to convert a RequestDeletionResource to a RequestStudentDataDeletionCommand.
 */
public class RequestStudentDataDeletionCommandFromResourceAssembler {
    /**
     * Converts a RequestDeletionResource to a RequestStudentDataDeletionCommand.
     *
     * @param resource The {@link RequestDeletionResource} resource to convert.
     * @return The {@link RequestStudentDataDeletionCommand} command.
     */
    public static RequestStudentDataDeletionCommand toCommandFromResource(RequestDeletionResource resource) {
        return new RequestStudentDataDeletionCommand(resource.studentId(), resource.requestedByProfileId());
    }
}
