package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.DriverStudentResource;

/**
 * Assembler to convert a Student entity to a DriverStudentResource.
 */
public class DriverStudentResourceFromEntityAssembler {
    /**
     * Converts a Student entity to a DriverStudentResource.
     *
     * @param entity The {@link Student} entity to convert.
     * @return The {@link DriverStudentResource} resource with the minimum information the driver needs.
     */
    public static DriverStudentResource toResourceFromEntity(Student entity) {
        return new DriverStudentResource(
                entity.getId(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getSchoolId(),
                entity.getStatus());
    }
}
