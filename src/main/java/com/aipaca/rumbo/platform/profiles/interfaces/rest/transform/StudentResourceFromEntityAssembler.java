package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.StudentResource;

/**
 * Assembler to convert a Student entity to a StudentResource.
 */
public class StudentResourceFromEntityAssembler {
    /**
     * Converts a Student entity to a StudentResource.
     *
     * @param entity The {@link Student} entity to convert.
     * @return The {@link StudentResource} resource.
     */
    public static StudentResource toResourceFromEntity(Student entity) {
        return new StudentResource(
                entity.getId(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getBirthDate(),
                entity.getStatus(),
                entity.getAnonymizedAt());
    }
}
