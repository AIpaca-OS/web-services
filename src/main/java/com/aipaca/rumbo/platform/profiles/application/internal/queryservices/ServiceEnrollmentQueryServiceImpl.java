package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;
import com.aipaca.rumbo.platform.profiles.application.queryservices.ServiceEnrollmentQueryService;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.repositories.*;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.*;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly=true)
public class ServiceEnrollmentQueryServiceImpl implements ServiceEnrollmentQueryService {
    private final ServiceEnrollmentRepository enrollments;
    private final StudentRepository students;
    private final ProfilesAuthorization auth;
    public ServiceEnrollmentQueryServiceImpl(ServiceEnrollmentRepository enrollments,
            StudentRepository students, ProfilesAuthorization auth) {
        this.enrollments=enrollments;this.students=students;this.auth=auth;
    }
    @Override public List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByDriverIdQuery query) {
        var driver=auth.currentDriver();
        if (!driver.getId().equals(query.driverId()))
            throw new ProfilesAccessException("Driver enrollments are not accessible",false);
        return enrollments.findAllByDriverProfileId(driver.getId());
    }
    @Override public List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByStudentIdQuery query) {
        var me=auth.currentProfile();
        var list=enrollments.findAllByStudentId(query.studentId());
        if (me.getProfileType()==ProfileType.TUTOR) {
            auth.requireStudentAccess(query.studentId());
            return list;
        }
        if (me.getProfileType()==ProfileType.DRIVER) {
            return list.stream().filter(e->e.getDriverProfileId().equals(me.getId())).toList();
        }
        throw new ProfilesAccessException("Enrollments are not accessible",false);
    }
    @Override public List<Student> handle(GetAllStudentsByDriverIdQuery query) {
        var me=auth.currentDriver();
        if (!me.getId().equals(query.driverId()))
            throw new ProfilesAccessException("Driver student list is not accessible",false);
        var ids=enrollments.findAllByDriverProfileId(me.getId()).stream()
                .filter(ServiceEnrollment::isActive).map(ServiceEnrollment::getStudentId).distinct().toList();
        return students.findAllById(ids).stream()
                .filter(s->s.getAnonymizedAt()==null && s.getStatus()==StudentStatus.ACTIVE).toList();
    }
}
