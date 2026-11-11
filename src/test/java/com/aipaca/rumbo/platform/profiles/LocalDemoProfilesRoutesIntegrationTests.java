package com.aipaca.rumbo.platform.profiles;

import com.aipaca.rumbo.platform.profiles.application.internal.access.LocalDemoActorIdentity;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.ProfilesController;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.SchoolsController;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.ServiceEnrollmentsController;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.StudentsController;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.CreateStudentResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.CreatedIdResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.RequestEnrollmentResource;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.SchoolResource;
import com.aipaca.rumbo.routes.application.ports.StudentAuthorizationPort;
import com.aipaca.rumbo.vehicles.domain.model.Vehicle;
import com.aipaca.rumbo.vehicles.domain.repositories.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises tutor registration, student ownership, accepted driver enrollment,
 * and Routes' Profiles ACL using fictional data in the CI PostgreSQL database.
 * Does not enable IAM or make unauthenticated student operations public.
 */
@SpringBootTest(properties = {
        "spring.profiles.active=local",
        "app.profiles.demo.enabled=true",
        "spring.jpa.hibernate.ddl-auto=update"
})
@Transactional
class LocalDemoProfilesRoutesIntegrationTests {
    @Autowired ProfilesController profiles;
    @Autowired SchoolsController schools;
    @Autowired StudentsController students;
    @Autowired ServiceEnrollmentsController enrollments;
    @Autowired StudentAuthorizationPort routeAcl;
    @Autowired ProfileRepository profileRepository;
    @Autowired VehicleRepository vehicleRepository;

    private void actor(String role) {
        var request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Rumbo-Demo-Role", role);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
    @AfterEach void clear() { RequestContextHolder.resetRequestAttributes(); }

    @Test void acceptedEnrollmentLinksAuthorizedStudentToDriverAndRoutes() {
        actor("tutor");
        assertEquals(HttpStatus.OK, profiles.getMine().getStatusCode());
        SchoolResource school = Objects.requireNonNull(schools.all().getBody()).stream()
                .filter(s -> "Colegio Demostración".equals(s.name()))
                .findFirst().orElseThrow();

        var response = students.register(new CreateStudentResource(
                "Estudiante", "Ficticio", LocalDate.of(2018, 4, 12),
                school.id(), RelationshipType.OTHER));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Long studentId = ((CreatedIdResource) response.getBody()).id();
        assertEquals(HttpStatus.OK, students.getById(studentId).getStatusCode());

        var driver = profileRepository.findByAccountId(
                new AccountId(LocalDemoActorIdentity.DRIVER_ACCOUNT_ID)).orElseThrow();
        var affiliation = enrollments.request(new RequestEnrollmentResource(studentId, driver.getId()));
        assertEquals(HttpStatus.CREATED, affiliation.getStatusCode());
        Long enrollmentId = ((CreatedIdResource) affiliation.getBody()).id();

        Vehicle vehicle = vehicleRepository.save(new Vehicle(
                "Toyota", "Hiace", "DM" + UUID.randomUUID().toString().substring(0, 6),
                12, 2024));

        actor("driver");
        assertFalse(routeAcl.isAuthorized(String.valueOf(studentId), String.valueOf(vehicle.getId()), null));
        assertEquals(HttpStatus.OK, enrollments.accept(enrollmentId).getStatusCode());
        assertTrue(routeAcl.isAuthorized(String.valueOf(studentId), String.valueOf(vehicle.getId()), null));
        assertTrue(routeAcl.getEligibleStudents(null).stream()
                .anyMatch(s -> s.id().equals(String.valueOf(studentId))));

        actor("tutor");
        assertThrows(ResponseStatusException.class, () -> routeAcl.getEligibleStudents(null));
    }
}
