package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.application.internal.commandservices.ServiceEnrollmentCommandServiceImpl;
import com.aipaca.rumbo.platform.profiles.application.security.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.profiles.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceEnrollmentCommandServiceImplTests {
    @Mock ServiceEnrollmentRepository enrollments;
    @Mock ProfileRepository profiles;
    @Mock ProfilesAuthorization auth;
    ServiceEnrollmentCommandServiceImpl service;
    static final Instant REQUESTED=Instant.parse("2026-11-10T10:00:00Z");
    @BeforeEach void setup() { service=new ServiceEnrollmentCommandServiceImpl(enrollments,profiles,auth); }
    private Profile tutor() { return Profile.reconstitute(5L,new AccountId(6L),ProfileType.TUTOR,
            new PersonName("Tutor","Uno"),"999999999"); }
    private Profile driver() { return Profile.reconstitute(8L,new AccountId(7L),ProfileType.DRIVER,
            new PersonName("Conductor","Uno"),"988888888"); }
    private ServiceEnrollment pending() { return ServiceEnrollment.reconstitute(15L,7L,8L,5L,
            ServiceEnrollment.PENDING,REQUESTED,null,null,null); }

    @Test void duplicateActivePairIsRejectedBeforeSave() {
        when(auth.currentTutor()).thenReturn(tutor());
        when(profiles.findById(8L)).thenReturn(Optional.of(driver()));
        when(enrollments.existsPendingOrAcceptedByStudentIdAndDriverProfileId(7L,8L)).thenReturn(true);
        var result=service.handle(new RequestServiceEnrollmentCommand(7L,8L,5L));
        assertTrue(result.isFailure());
        verify(enrollments,never()).save(any());
    }

    @Test void requesterCanCancelPendingEnrollment() {
        when(auth.currentTutor()).thenReturn(tutor());
        when(enrollments.findById(15L)).thenReturn(Optional.of(pending()));
        when(enrollments.save(any(ServiceEnrollment.class))).thenAnswer(call -> call.getArgument(0));
        var result=service.handle(new CancelServiceEnrollmentCommand(15L,5L));
        assertTrue(result.isSuccess());
        verify(enrollments).save(argThat(e -> ServiceEnrollment.CANCELLED.equals(e.getStatus())));
    }

    @Test void differentAuthenticatedTutorCannotCancelAsRequester() {
        when(auth.currentTutor()).thenReturn(tutor());
        var result=service.handle(new CancelServiceEnrollmentCommand(15L,999L));
        assertTrue(result.isFailure());
        verifyNoInteractions(enrollments);
    }

    @Test void unrelatedDriverCannotAcceptEnrollment() {
        when(auth.currentDriver()).thenReturn(driver());
        var result=service.handle(new AcceptServiceEnrollmentCommand(15L,999L));
        assertTrue(result.isFailure());
        verifyNoInteractions(enrollments);
    }
}
