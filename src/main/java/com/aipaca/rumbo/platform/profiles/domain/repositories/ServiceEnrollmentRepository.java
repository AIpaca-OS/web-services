package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;

import java.util.List;
import java.util.Optional;

/**
 * Service enrollment repository port.
 */
public interface ServiceEnrollmentRepository {
    Optional<ServiceEnrollment> findById(Long id);

    List<ServiceEnrollment> findAllByDriverProfileId(Long driverProfileId);

    List<ServiceEnrollment> findAllByStudentId(Long studentId);

    /**
     * Checks whether the student-driver pair already has a PENDING or ACCEPTED enrollment.
     * ENDED and CANCELLED enrollments do not count.
     *
     * @param studentId       The student id.
     * @param driverProfileId The driver profile id.
     * @return {@code true} when a conflicting enrollment exists.
     */
    boolean existsPendingOrAcceptedByStudentIdAndDriverProfileId(Long studentId, Long driverProfileId);

    ServiceEnrollment save(ServiceEnrollment enrollment);
}
