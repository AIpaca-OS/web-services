package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;

import java.util.List;
import java.util.Optional;

/** Service enrollment repository port. Actor authorization belongs to application services. */
public interface ServiceEnrollmentRepository {
    Optional<ServiceEnrollment> findById(Long id);

    List<ServiceEnrollment> findAllByDriverProfileId(Long driverProfileId);

    List<ServiceEnrollment> findAllByStudentId(Long studentId);

    /**
     * Checks for a conflicting request or accepted service for this exact pair.
     * ENDED enrollments and enrollments with a different driver do not count.
     * This pre-check alone does not prevent concurrent duplicate writes.
     */
    boolean existsPendingOrAcceptedByStudentIdAndDriverProfileId(Long studentId, Long driverProfileId);

    ServiceEnrollment save(ServiceEnrollment enrollment);
}
