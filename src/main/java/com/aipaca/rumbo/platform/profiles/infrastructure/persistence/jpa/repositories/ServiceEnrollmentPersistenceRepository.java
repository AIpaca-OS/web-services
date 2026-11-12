package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ServiceEnrollmentStatus;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ServiceEnrollmentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data access for enrollment rows, including retained ENDED history.
 * The active-pair existence check is a precondition, not a concurrency guarantee.
 */
@Repository
public interface ServiceEnrollmentPersistenceRepository
        extends JpaRepository<ServiceEnrollmentPersistenceEntity, Long> {
    List<ServiceEnrollmentPersistenceEntity> findAllByDriverProfileId(Long driverProfileId);

    List<ServiceEnrollmentPersistenceEntity> findAllByStudentId(Long studentId);

    boolean existsByStudentIdAndDriverProfileIdAndStatusIn(
            Long studentId, Long driverProfileId, Collection<ServiceEnrollmentStatus> statuses);
}
