package com.aipaca.rumbo.routes.infrastructure.integrations;

import com.aipaca.rumbo.platform.profiles.application.internal.access.ActorIdentity;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ServiceEnrollmentRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.routes.application.ports.StudentAuthorizationPort;
import com.aipaca.rumbo.vehicles.domain.repositories.VehicleRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * In-process bounded-context ACL for the *local, loopback-only* demo.
 * Requires accepted enrollment, active student, approved tutor and a persisted vehicle.
 * This is NOT production authorization or vehicle ownership proof.
 */
@Component
@Primary
@org.springframework.context.annotation.Profile("local")
@ConditionalOnProperty(prefix="app.profiles.demo",name="enabled",havingValue="true")
@Transactional(readOnly = true)
public class LocalDemoStudentAuthorization implements StudentAuthorizationPort {
    private final ActorIdentity actor;
    private final ProfileRepository profiles;
    private final StudentRepository students;
    private final ServiceEnrollmentRepository enrollments;
    private final VehicleRepository vehicles;

    public LocalDemoStudentAuthorization(ActorIdentity actor,ProfileRepository profiles,
            StudentRepository students,ServiceEnrollmentRepository enrollments,VehicleRepository vehicles) {
        this.actor=actor;this.profiles=profiles;this.students=students;
        this.enrollments=enrollments;this.vehicles=vehicles;
    }

    private Optional<Profile> currentDriver() {
        if (!actor.hasRole("DRIVER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Local demo driver role required");
        }
        return profiles.findByAccountId(new AccountId(actor.currentAccountId()))
                .filter(p -> p.getProfileType()==ProfileType.DRIVER);
    }
    private boolean eligible(Student student) {
        return student.getStatus()==StudentStatus.ACTIVE &&
                student.getAnonymizedAt()==null &&
                student.getTutorRelationships().stream().anyMatch(TutorStudentRelationship::isAuthorized);
    }
    private static Long positiveId(String value) {
        if (value==null || !value.matches("[1-9][0-9]*")) return null;
        try { return Long.valueOf(value); } catch (NumberFormatException ex) { return null; }
    }
    @Override
    public boolean canManageVehicle(String vehicleId,String ignoredBearer) {
        Long id=positiveId(vehicleId);
        return id!=null && currentDriver().isPresent() && vehicles.existsById(id);
    }
    @Override
    public boolean isAuthorized(String studentId,String vehicleId,String ignoredBearer) {
        if (!canManageVehicle(vehicleId,ignoredBearer)) return false;
        Long id=positiveId(studentId);
        if(id==null) return false;
        Student student=students.findById(id).orElse(null);
        if(student==null || !eligible(student)) return false;
        long driverId=currentDriver().orElseThrow().getId();
        return enrollments.findAllByDriverProfileId(driverId).stream()
                .anyMatch(e -> e.isActive() && e.getStudentId().equals(id));
    }
    @Override
    public List<EligibleStudent> getEligibleStudents(String ignoredBearer) {
        Profile driver=currentDriver().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Create a local demo driver profile first"));
        List<Long> ids=enrollments.findAllByDriverProfileId(driver.getId()).stream()
                .filter(ServiceEnrollment::isActive).map(ServiceEnrollment::getStudentId).distinct().toList();
        if(ids.isEmpty())return List.of();
        return students.findAllById(ids).stream().filter(this::eligible)
                .map(s -> new EligibleStudent(String.valueOf(s.getId()),s.getFullName()))
                .toList();
    }
}
