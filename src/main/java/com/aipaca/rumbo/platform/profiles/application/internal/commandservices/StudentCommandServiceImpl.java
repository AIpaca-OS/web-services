package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;
import com.aipaca.rumbo.platform.profiles.application.commandservices.StudentCommandService;
import com.aipaca.rumbo.platform.profiles.application.internal.ProfilesResults;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentCommandServiceImpl implements StudentCommandService {
    private final StudentRepository students;
    private final SchoolRepository schools;
    private final ProfileRepository profiles;
    private final ProfilesAuthorization authorization;

    public StudentCommandServiceImpl(StudentRepository students,SchoolRepository schools,
            ProfileRepository profiles, ProfilesAuthorization authorization) {
        this.students=students;this.schools=schools;this.profiles=profiles;
        this.authorization=authorization;
    }
    private boolean schoolMissing(Long schoolId) { return schoolId != null && schools.findById(schoolId).isEmpty(); }

    @Override @Transactional
    public Result<Long,ApplicationError> handle(RegisterStudentCommand command) {
        var tutor=authorization.currentTutor();
        if (!tutor.getId().equals(command.tutorProfileId()))
            return ProfilesResults.conflict("STUDENT", "Registering tutor must be the authenticated tutor");
        if (schoolMissing(command.schoolId())) return ProfilesResults.notFound("SCHOOL", command.schoolId());
        // Student itself creates its first AUTHORIZED child relationship (US06).
        var student=new Student(command);
        var saved=students.save(student);
        var firstRelationship=saved.getTutorRelationships().stream()
                .filter(r -> r.isAuthorized() && r.getTutorProfileId().equals(tutor.getId()))
                .findFirst().orElseThrow(() -> new IllegalStateException("Initial tutor authorization missing"));
        return Result.success(saved.getId());
    }

    @Override @Transactional
    public Result<Long,ApplicationError> handle(UpdateStudentInformationCommand command) {
        var student=authorization.requireStudentAccess(command.studentId());
        if (schoolMissing(command.schoolId())) return ProfilesResults.notFound("SCHOOL", command.schoolId());
        try { student.updateInformation(command.firstName(), command.lastName(), command.birthDate(), command.schoolId()); }
        catch (IllegalStateException ex) { return ProfilesResults.domainFailure(ex); }
        return Result.success(students.save(student).getId());
    }

    @Override @Transactional
    public Result<Long,ApplicationError> handle(AuthorizeTutorStudentRelationshipCommand command) {
        var me=authorization.currentTutor();
        if (!me.getId().equals(command.authorizedByProfileId()))
            return ProfilesResults.conflict("RELATIONSHIP","Authorizer must be the authenticated tutor");
        var student=authorization.requireStudentAccess(command.studentId());
        var target=profiles.findById(command.tutorProfileId());
        if (target.isEmpty()) return ProfilesResults.notFound("PROFILE",command.tutorProfileId());
        if (target.get().getProfileType()!=ProfileType.TUTOR)
            return ProfilesResults.invalid("Tutor", "Target profile must have TUTOR type");
        var now=java.time.Instant.now();
        try { student.authorizeTutor(command.tutorProfileId(), command.relationshipType(), me.getId(), now); }
        catch (IllegalStateException ex) { return ProfilesResults.domainFailure(ex); }
        var saved=students.save(student);
        return Result.success(saved.getId());
    }

    @Override @Transactional
    public Result<Long,ApplicationError> handle(RevokeTutorStudentRelationshipCommand command) {
        var me=authorization.currentTutor();
        if (!me.getId().equals(command.requestedByProfileId()))
            return ProfilesResults.conflict("RELATIONSHIP","Revoker must be the authenticated tutor");
        var student=authorization.requireStudentAccess(command.studentId());
        var now=java.time.Instant.now();
        try { student.revokeTutor(command.tutorProfileId(),me.getId(),now); }
        catch (IllegalStateException ex) { return ProfilesResults.domainFailure(ex); }
        var saved=students.save(student);
        return Result.success(saved.getId());
    }
}
