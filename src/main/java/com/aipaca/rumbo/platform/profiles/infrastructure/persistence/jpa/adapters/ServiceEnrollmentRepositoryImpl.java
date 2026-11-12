package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ServiceEnrollmentRepository;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.ServiceEnrollmentPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.ServiceEnrollmentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Bridges the enrollment domain port to Spring Data without granting actor permissions. */
@Repository
public class ServiceEnrollmentRepositoryImpl implements ServiceEnrollmentRepository {
    private static final Set<String> CONFLICTING_STATUSES =
            Set.of(ServiceEnrollment.PENDING, ServiceEnrollment.ACCEPTED);

    private final ServiceEnrollmentPersistenceRepository serviceEnrollmentPersistenceRepository;

    public ServiceEnrollmentRepositoryImpl(ServiceEnrollmentPersistenceRepository serviceEnrollmentPersistenceRepository) {
        this.serviceEnrollmentPersistenceRepository = serviceEnrollmentPersistenceRepository;
    }

    @Override
    public Optional<ServiceEnrollment> findById(Long id) {
        return serviceEnrollmentPersistenceRepository.findById(id)
                .map(ServiceEnrollmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<ServiceEnrollment> findAllByDriverProfileId(Long driverProfileId) {
        return serviceEnrollmentPersistenceRepository.findAllByDriverProfileId(driverProfileId).stream()
                .map(ServiceEnrollmentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<ServiceEnrollment> findAllByStudentId(Long studentId) {
        return serviceEnrollmentPersistenceRepository.findAllByStudentId(studentId).stream()
                .map(ServiceEnrollmentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsPendingOrAcceptedByStudentIdAndDriverProfileId(Long studentId, Long driverProfileId) {
        return serviceEnrollmentPersistenceRepository.existsByStudentIdAndDriverProfileIdAndStatusIn(
                studentId, driverProfileId, CONFLICTING_STATUSES);
    }

    @Override
    public ServiceEnrollment save(ServiceEnrollment enrollment) {
        var saved = serviceEnrollmentPersistenceRepository.save(
                ServiceEnrollmentPersistenceAssembler.toPersistenceFromDomain(enrollment));
        return ServiceEnrollmentPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
