package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;

import com.aipaca.rumbo.platform.profiles.application.queryservices.ServiceEnrollmentQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllStudentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetServiceEnrollmentByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ServiceEnrollmentRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Enrollment Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class ServiceEnrollmentQueryServiceImpl implements ServiceEnrollmentQueryService {
    private final ServiceEnrollmentRepository serviceEnrollmentRepository;
    private final StudentRepository studentRepository;

    /**
     * Constructor
     *
     * @param serviceEnrollmentRepository The {@link ServiceEnrollmentRepository} instance
     * @param studentRepository           The {@link StudentRepository} instance
     */
    public ServiceEnrollmentQueryServiceImpl(ServiceEnrollmentRepository serviceEnrollmentRepository,
                                             StudentRepository studentRepository) {
        this.serviceEnrollmentRepository = serviceEnrollmentRepository;
        this.studentRepository = studentRepository;
    }

    // inherited javadoc
    @Override
    public Optional<ServiceEnrollment> handle(GetServiceEnrollmentByIdQuery query) {
        return serviceEnrollmentRepository.findById(query.enrollmentId());
    }

    // inherited javadoc
    @Override
    public List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByDriverIdQuery query) {
        return serviceEnrollmentRepository.findAllByDriverProfileId(query.driverId());
    }

    // inherited javadoc
    @Override
    public List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByStudentIdQuery query) {
        return serviceEnrollmentRepository.findAllByStudentId(query.studentId());
    }

    // inherited javadoc
    @Override
    public List<Student> handle(GetAllStudentsByDriverIdQuery query) {
        var studentIds = serviceEnrollmentRepository.findAllByDriverProfileId(query.driverId()).stream()
                .filter(enrollment -> enrollment.isPending() || enrollment.isActive())
                .map(ServiceEnrollment::getStudentId)
                .distinct()
                .toList();
        return studentRepository.findAllById(studentIds).stream()
                .filter(student -> student.getStatus() == StudentStatus.ACTIVE && student.getAnonymizedAt() == null)
                .toList();
    }
}
