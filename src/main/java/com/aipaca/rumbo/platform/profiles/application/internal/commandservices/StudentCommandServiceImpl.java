package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;

import com.aipaca.rumbo.platform.profiles.application.commandservices.StudentCommandService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RevokeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateStudentInformationCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.SchoolRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Student Command Service Implementation
 */
@Service
@Transactional
public class StudentCommandServiceImpl implements StudentCommandService {
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final ProfileRepository profileRepository;

    /**
     * Constructor
     *
     * @param studentRepository The {@link StudentRepository} instance
     * @param schoolRepository  The {@link SchoolRepository} instance
     * @param profileRepository The {@link ProfileRepository} instance
     */
    public StudentCommandServiceImpl(StudentRepository studentRepository, SchoolRepository schoolRepository,
                                     ProfileRepository profileRepository) {
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.profileRepository = profileRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(RegisterStudentCommand command) {
        if (!isTutor(command.tutorProfileId())) {
            return Result.failure(ApplicationError.validationError("tutorProfileId", "Existing TUTOR profile required"));
        }
        if (isSchoolMissing(command.schoolId())) {
            return Result.failure(ApplicationError.notFound("School", String.valueOf(command.schoolId())));
        }
        try {
            var savedStudent = studentRepository.save(new Student(command));
            return Result.success(savedStudent.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Student", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("register-student", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(UpdateStudentInformationCommand command) {
        var result = studentRepository.findById(command.studentId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Student", command.studentId().toString()));
        }
        if (isSchoolMissing(command.schoolId())) {
            return Result.failure(ApplicationError.notFound("School", String.valueOf(command.schoolId())));
        }
        var student = result.get();
        try {
            student.updateInformation(command.firstName(), command.lastName(), command.birthDate(), command.schoolId());
            return Result.success(studentRepository.save(student).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Student", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Student update", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-student", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(AuthorizeTutorStudentRelationshipCommand command) {
        var result = studentRepository.findById(command.studentId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Student", command.studentId().toString()));
        }
        if (!isTutor(command.authorizedByProfileId()) || !isTutor(command.tutorProfileId())) {
            return Result.failure(ApplicationError.validationError("Tutor", "Both the authorizer and target must be TUTOR profiles"));
        }
        var student = result.get();
        try {
            // The Student aggregate requires an already authorized tutor to add another one.
            student.authorizeTutor(command.tutorProfileId(), command.relationshipType(),
                    command.authorizedByProfileId(), Instant.now());
            return Result.success(studentRepository.save(student).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Tutor authorization", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Tutor authorization", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("authorize-tutor", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(RevokeTutorStudentRelationshipCommand command) {
        var result = studentRepository.findById(command.studentId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Student", command.studentId().toString()));
        }
        var student = result.get();
        try {
            // The Student aggregate forbids a revocation by a non-authorized tutor or of the last tutor.
            student.revokeTutor(command.tutorProfileId(), command.requestedByProfileId(), Instant.now());
            return Result.success(studentRepository.save(student).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Tutor revocation", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Tutor revocation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("revoke-tutor", e.getMessage()));
        }
    }

    private boolean isSchoolMissing(Long schoolId) {
        return schoolId != null && schoolRepository.findById(schoolId).isEmpty();
    }

    private boolean isTutor(Long profileId) {
        return profileRepository.findById(profileId)
                .filter(profile -> profile.getProfileType() == ProfileType.TUTOR)
                .isPresent();
    }
}
