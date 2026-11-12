package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.CreateStudentResource;

/**
 * Assembler to convert a CreateStudentResource to a RegisterStudentCommand.
 */
public class RegisterStudentCommandFromResourceAssembler {
    /**
     * Converts a CreateStudentResource to a RegisterStudentCommand.
     *
     * @param resource The {@link CreateStudentResource} resource to convert.
     * @return The {@link RegisterStudentCommand} command.
     */
    public static RegisterStudentCommand toCommandFromResource(CreateStudentResource resource) {
        return new RegisterStudentCommand(
                resource.tutorProfileId(),
                resource.firstName(),
                resource.lastName(),
                resource.birthDate(),
                resource.schoolId(),
                resource.relationshipType());
    }
}
