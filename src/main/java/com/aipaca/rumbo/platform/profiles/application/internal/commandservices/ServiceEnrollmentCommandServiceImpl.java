package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;

import com.aipaca.rumbo.platform.profiles.application.commandservices.ServiceEnrollmentCommandService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AcceptServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CancelServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.EndServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ServiceEnrollmentRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service Enrollment Command Service Implementation
 */
@Service
@Transactional
public class ServiceEnrollmentCommandServiceImpl implements ServiceEnrollmentCommandService {
    private final ServiceEnrollmentRepository serviceEnrollmentRepository;
    private final ProfileRepository profileRepository;
    private final StudentRepository studentRepository;

    /**
     * Constructor
     *
     * @param serviceEnrollmentRepository The {@link ServiceEnrollmentRepository} instance
     * @param profileRepository           The {@link ProfileRepository} instance
     * @param studentRepository           The {@link StudentRepository} instance
     */
    public ServiceEnrollmentCommandServiceImpl(ServiceEnrollmentRepository serviceEnrollmentRepository,
                                               ProfileRepository profileRepository,
                                               StudentRepository studentRepository) {
        this.serviceEnrollmentRepository = serviceEnrollmentRepository;
        this.profileRepository = profileRepository;
        this.studentRepository = studentRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(RequestServiceEnrollmentCommand command) {
        var tutor = profileRepository.findById(command.requestedByProfileId());
        if (tutor.isEmpty() || tutor.get().getProfileType() != ProfileType.TUTOR) {
            return Result.failure(ApplicationError.validationError("requestedByProfileId", "Existing TUTOR profile required"));
        }
        var student = studentRepository.findById(command.studentId());
        if (student.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Student", command.studentId().toString()));
        }
        if (student.get().getStatus() != StudentStatus.ACTIVE || student.get().getAnonymizedAt() != null
                || !student.get().hasAuthorizedTutor(command.requestedByProfileId())) {
            return Result.failure(ApplicationError.businessRuleViolation("Enrollment",
                    "Requester must be an authorized tutor of an active student"));
        }
        var driver = profileRepository.findById(command.driverProfileId());
        if (driver.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Profile", command.driverProfileId().toString()));
        }
        if (driver.get().getProfileType() != ProfileType.DRIVER) {
            return Result.failure(ApplicationError.validationError("driverProfileId", "DRIVER profile required"));
        }
        if (serviceEnrollmentRepository.existsPendingOrAcceptedByStudentIdAndDriverProfileId(
                command.studentId(), command.driverProfileId())) {
            return Result.failure(ApplicationError.conflict("Enrollment",
                    "This student already has a pending or accepted enrollment with that driver"));
        }
        try {
            var savedEnrollment = serviceEnrollmentRepository.save(new ServiceEnrollment(command));
            return Result.success(savedEnrollment.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Enrollment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("request-enrollment", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(AcceptServiceEnrollmentCommand command) {
        var result = serviceEnrollmentRepository.findById(command.enrollmentId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Enrollment", command.enrollmentId().toString()));
        }
        var enrollment = result.get();
        if (!enrollment.getDriverProfileId().equals(command.driverProfileId())) {
            return Result.failure(ApplicationError.conflict("Enrollment", "Driver does not match the enrollment"));
        }
        try {
            enrollment.accept(Instant.now());
            return Result.success(serviceEnrollmentRepository.save(enrollment).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Enrollment acceptance", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Enrollment acceptance", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("accept-enrollment", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CancelServiceEnrollmentCommand command) {
        var result = serviceEnrollmentRepository.findById(command.enrollmentId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Enrollment", command.enrollmentId().toString()));
        }
        var enrollment = result.get();
        try {
            enrollment.cancel(command.requestedByProfileId(), Instant.now());
            return Result.success(serviceEnrollmentRepository.save(enrollment).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Enrollment cancellation", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Enrollment cancellation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("cancel-enrollment", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(EndServiceEnrollmentCommand command) {
        var result = serviceEnrollmentRepository.findById(command.enrollmentId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Enrollment", command.enrollmentId().toString()));
        }
        var enrollment = result.get();
        var actor = profileRepository.findById(command.requestedByProfileId());
        if (actor.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Profile", command.requestedByProfileId().toString()));
        }
        boolean isEnrollmentDriver = actor.get().getProfileType() == ProfileType.DRIVER
                && actor.get().getId().equals(enrollment.getDriverProfileId());
        boolean isAuthorizedTutor = actor.get().getProfileType() == ProfileType.TUTOR
                && studentRepository.findById(enrollment.getStudentId())
                        .map(student -> student.hasAuthorizedTutor(actor.get().getId()))
                        .orElse(false);
        if (!isEnrollmentDriver && !isAuthorizedTutor) {
            return Result.failure(ApplicationError.businessRuleViolation("Enrollment termination",
                    "Only its driver or an authorized tutor may end the enrollment"));
        }
        try {
            enrollment.end(Instant.now());
            return Result.success(serviceEnrollmentRepository.save(enrollment).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Enrollment termination", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Enrollment termination", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("end-enrollment", e.getMessage()));
        }
    }
}
