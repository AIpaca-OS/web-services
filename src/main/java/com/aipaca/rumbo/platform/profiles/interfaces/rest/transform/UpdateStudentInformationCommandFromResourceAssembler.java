package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateStudentInformationCommand;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.UpdateStudentResource;

/**
 * Assembler to convert an UpdateStudentResource to an UpdateStudentInformationCommand.
 */
public class UpdateStudentInformationCommandFromResourceAssembler {
    /**
     * Converts an UpdateStudentResource to an UpdateStudentInformationCommand.
     *
     * @param studentId The student id taken from the path.
     * @param resource The {@link UpdateStudentResource} resource to convert.
     * @return The {@link UpdateStudentInformationCommand} command.
     */
    public static UpdateStudentInformationCommand toCommandFromResource(Long studentId, UpdateStudentResource resource) {
        return new UpdateStudentInformationCommand(
                studentId,
                resource.firstName(),
                resource.lastName(),
                resource.birthDate());
    }
}
