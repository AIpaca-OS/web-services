package com.aipaca.rumbo.platform.routes.infrastructure.integrations;

import com.aipaca.rumbo.platform.profiles.application.internal.access.ActorIdentity;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ServiceEnrollmentRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.platform.vehicles.domain.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalDemoStudentAuthorizationTests {
    @Test void missingDriverRoleIsRejected() {
        var actor=mock(ActorIdentity.class);
        when(actor.hasRole("DRIVER")).thenReturn(false);
        var acl=new LocalDemoStudentAuthorization(actor,mock(ProfileRepository.class),
                mock(StudentRepository.class),mock(ServiceEnrollmentRepository.class),mock(VehicleRepository.class));
        assertThrows(ResponseStatusException.class,() -> acl.getEligibleStudents(null));
        assertThrows(ResponseStatusException.class,() -> acl.canManageVehicle("1",null));
    }
    @Test void unregisteredVehicleCannotBeManaged() {
        var actor=mock(ActorIdentity.class);
        when(actor.hasRole("DRIVER")).thenReturn(true);
        var acl=new LocalDemoStudentAuthorization(actor,mock(ProfileRepository.class),
                mock(StudentRepository.class),mock(ServiceEnrollmentRepository.class),mock(VehicleRepository.class));
        assertFalse(acl.canManageVehicle("invalid-id",null));
        assertFalse(acl.isAuthorized("1","invalid-id",null));
    }
}
