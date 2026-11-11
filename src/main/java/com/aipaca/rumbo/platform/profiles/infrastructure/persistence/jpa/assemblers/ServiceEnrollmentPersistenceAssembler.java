package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ServiceEnrollmentPersistenceEntity;

/** Translates enrollment state without accepting, ending, or changing its timestamps. */
public final class ServiceEnrollmentPersistenceAssembler {
    private ServiceEnrollmentPersistenceAssembler() {
    }

    public static ServiceEnrollment toDomainFromPersistence(ServiceEnrollmentPersistenceEntity entity) {
        if (entity == null) return null;
        return ServiceEnrollment.reconstitute(
                entity.getId(), entity.getStudentId(), entity.getDriverProfileId(),
                entity.getRequestedByProfileId(), entity.getStatus(), entity.getRequestedAt(),
                entity.getAcceptedAt(), entity.getCancelledAt(), entity.getEndedAt());
    }

    public static ServiceEnrollmentPersistenceEntity toPersistenceFromDomain(ServiceEnrollment enrollment) {
        var entity = new ServiceEnrollmentPersistenceEntity();
        entity.setId(enrollment.getId());
        entity.setStudentId(enrollment.getStudentId());
        entity.setDriverProfileId(enrollment.getDriverProfileId());
        entity.setRequestedByProfileId(enrollment.getRequestedByProfileId());
        entity.setStatus(enrollment.getStatus());
        entity.setRequestedAt(enrollment.getRequestedAt());
        entity.setAcceptedAt(enrollment.getAcceptedAt());
        entity.setCancelledAt(enrollment.getCancelledAt());
        entity.setEndedAt(enrollment.getEndedAt());
        return entity;
    }
}
