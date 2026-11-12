package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ServiceEnrollmentResource;

/**
 * Assembler to convert a ServiceEnrollment entity to a ServiceEnrollmentResource.
 */
public class ServiceEnrollmentResourceFromEntityAssembler {
    /**
     * Converts a ServiceEnrollment entity to a ServiceEnrollmentResource.
     *
     * @param entity The {@link ServiceEnrollment} entity to convert.
     * @return The {@link ServiceEnrollmentResource} resource.
     */
    public static ServiceEnrollmentResource toResourceFromEntity(ServiceEnrollment entity) {
        return new ServiceEnrollmentResource(
                entity.getId(),
                entity.getStudentId(),
                entity.getDriverProfileId(),
                entity.getRequestedByProfileId(),
                entity.getStatus(),
                entity.getRequestedAt(),
                entity.getAcceptedAt(),
                entity.getCancelledAt(),
                entity.getEndedAt());
    }
}
