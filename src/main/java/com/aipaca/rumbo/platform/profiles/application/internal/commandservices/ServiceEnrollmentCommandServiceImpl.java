package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;
import com.aipaca.rumbo.platform.profiles.application.commandservices.ServiceEnrollmentCommandService;
import com.aipaca.rumbo.platform.profiles.application.internal.ProfilesResults;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class ServiceEnrollmentCommandServiceImpl implements ServiceEnrollmentCommandService {
    private final ServiceEnrollmentRepository enrollments;
    private final ProfileRepository profiles;
    private final ProfilesAuthorization auth;
    public ServiceEnrollmentCommandServiceImpl(ServiceEnrollmentRepository enrollments,
            ProfileRepository profiles, ProfilesAuthorization auth) {
        this.enrollments=enrollments;this.profiles=profiles;this.auth=auth;
    }
    @Override @Transactional
    public Result<Long,ApplicationError> handle(RequestServiceEnrollmentCommand command) {
        var tutor=auth.currentTutor();
        if (!tutor.getId().equals(command.requestedByProfileId()))
            return ProfilesResults.conflict("ENROLLMENT","Requester must be the authenticated tutor");
        auth.requireStudentAccess(command.studentId());
        var driver=profiles.findById(command.driverProfileId());
        if (driver.isEmpty()) return ProfilesResults.notFound("PROFILE", command.driverProfileId());
        if (driver.get().getProfileType()!=ProfileType.DRIVER)
            return ProfilesResults.invalid("Driver", "Target must have DRIVER profile type");
        if (enrollments.existsPendingOrAcceptedByStudentIdAndDriverProfileId(
                command.studentId(), command.driverProfileId()))
            return ProfilesResults.conflict("ENROLLMENT", "A pending or accepted enrollment already exists for this student and driver");
        var entity=new com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment(command);
        // A PostgreSQL partial unique index is still needed to enforce this under concurrency.
        return Result.success(enrollments.save(entity).getId());
    }
    @Override @Transactional
    public Result<Long,ApplicationError> handle(AcceptServiceEnrollmentCommand command) {
        var driver=auth.currentDriver();
        if (!driver.getId().equals(command.driverProfileId()))
            return ProfilesResults.conflict("ENROLLMENT","Only the requested driver may accept");
        var opt=enrollments.findById(command.enrollmentId());
        if (opt.isEmpty()) return ProfilesResults.notFound("ENROLLMENT",command.enrollmentId());
        var enrollment=opt.get();
        if (!driver.getId().equals(enrollment.getDriverProfileId()))
            return ProfilesResults.conflict("ENROLLMENT","Enrollment belongs to another driver");
        try { enrollment.accept(Instant.now()); }
        catch (IllegalStateException ex) { return ProfilesResults.domainFailure(ex); }
        return Result.success(enrollments.save(enrollment).getId());
    }
    @Override @Transactional
    public Result<Long,ApplicationError> handle(CancelServiceEnrollmentCommand command) {
        var me=auth.currentTutor();
        if (!me.getId().equals(command.requestedByProfileId()))
            return ProfilesResults.conflict("ENROLLMENT","Requester does not match authenticated tutor");
        var opt=enrollments.findById(command.enrollmentId());
        if (opt.isEmpty()) return ProfilesResults.notFound("ENROLLMENT",command.enrollmentId());
        var enrollment=opt.get();
        try { enrollment.cancel(me.getId(),Instant.now()); }
        catch (IllegalStateException ex) { return ProfilesResults.domainFailure(ex); }
        return Result.success(enrollments.save(enrollment).getId());
    }
    @Override @Transactional
    public Result<Long,ApplicationError> handle(EndServiceEnrollmentCommand command) {
        var me=auth.currentProfile();
        if (!me.getId().equals(command.requestedByProfileId()))
            return ProfilesResults.conflict("ENROLLMENT","Actor does not match authenticated user");
        var opt=enrollments.findById(command.enrollmentId());
        if (opt.isEmpty()) return ProfilesResults.notFound("ENROLLMENT",command.enrollmentId());
        var enrollment=opt.get();
        if (me.getProfileType()==ProfileType.DRIVER) {
            if (!me.getId().equals(enrollment.getDriverProfileId()))
                throw new com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException("Enrollment is not accessible", false);
        } else if (me.getProfileType()==ProfileType.TUTOR) {
            auth.requireStudentAccess(enrollment.getStudentId());
        } else throw new com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException("Actor not eligible", false);
        try { enrollment.end(Instant.now()); }
        catch (IllegalStateException ex) { return ProfilesResults.domainFailure(ex); }
        return Result.success(enrollments.save(enrollment).getId());
    }
}
