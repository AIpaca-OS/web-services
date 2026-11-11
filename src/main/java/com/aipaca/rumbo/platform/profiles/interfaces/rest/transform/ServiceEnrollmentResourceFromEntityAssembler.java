package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ServiceEnrollmentResource;
public final class ServiceEnrollmentResourceFromEntityAssembler {
    private ServiceEnrollmentResourceFromEntityAssembler() { }
    public static ServiceEnrollmentResource toResourceFromEntity(ServiceEnrollment e) {
        return new ServiceEnrollmentResource(e.getId(),e.getStudentId(),e.getDriverProfileId(),e.getRequestedByProfileId(),e.getStatus(),e.getRequestedAt(),e.getAcceptedAt(),e.getCancelledAt(),e.getEndedAt());
    }
}
