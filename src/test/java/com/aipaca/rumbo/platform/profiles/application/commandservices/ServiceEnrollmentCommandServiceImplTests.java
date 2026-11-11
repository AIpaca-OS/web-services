package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.application.internal.commandservices.ServiceEnrollmentCommandServiceImpl;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.profiles.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceEnrollmentCommandServiceImplTests {
    @Mock ServiceEnrollmentRepository enrollments;
    @Mock ProfileRepository profiles;
    @Mock StudentRepository students;
    ServiceEnrollmentCommandServiceImpl service;
    static final Instant AT = Instant.parse("2026-11-10T10:00:00Z");

    @BeforeEach void setup() {
        service = new ServiceEnrollmentCommandServiceImpl(enrollments, profiles, students);
    }

    private Profile profile(Long id, ProfileType type) {
        return Profile.reconstitute(id, new AccountId(id + 10), type,
                new PersonName("Perfil", "Prueba"), "999999999");
    }

    private Student student() {
        var link = TutorStudentRelationship.reconstitute(31L,5L,5L,
                RelationshipType.MOTHER,AuthorizationStatus.AUTHORIZED,AT,null);
        return Student.reconstitute(7L,new PersonName("Niño","Uno"),LocalDate.of(2018,5,1),
                null,StudentStatus.ACTIVE,null,0L,0L,List.of(link));
    }

    private ServiceEnrollment pending() {
        return ServiceEnrollment.reconstitute(15L,7L,8L,5L,
                ServiceEnrollmentStatus.PENDING,AT,null,null,null);
    }

    @Test void duplicateActivePairIsRejected() {
        when(profiles.findById(5L)).thenReturn(Optional.of(profile(5L,ProfileType.TUTOR)));
        when(students.findById(7L)).thenReturn(Optional.of(student()));
        when(profiles.findById(8L)).thenReturn(Optional.of(profile(8L,ProfileType.DRIVER)));
        when(enrollments.existsPendingOrAcceptedByStudentIdAndDriverProfileId(7L,8L)).thenReturn(true);
        var result=service.handle(new RequestServiceEnrollmentCommand(7L,8L,5L));
        assertTrue(result.isFailure());
        verify(enrollments,never()).save(any());
    }

    @Test void requesterMayCancelPendingEnrollment() {
        when(enrollments.findById(15L)).thenReturn(Optional.of(pending()));
        when(enrollments.save(any(ServiceEnrollment.class))).thenAnswer(call -> call.getArgument(0));
        var result=service.handle(new CancelServiceEnrollmentCommand(15L,5L));
        assertTrue(result.isSuccess());
        verify(enrollments).save(argThat(e -> ServiceEnrollmentStatus.CANCELLED.equals(e.getStatus())));
    }

    @Test void differentRequesterCannotCancel() {
        when(enrollments.findById(15L)).thenReturn(Optional.of(pending()));
        var result=service.handle(new CancelServiceEnrollmentCommand(15L,999L));
        assertTrue(result.isFailure());
        verify(enrollments,never()).save(any());
    }

    @Test void driverIdMustMatchEnrollmentToAccept() {
        when(enrollments.findById(15L)).thenReturn(Optional.of(pending()));
        var result=service.handle(new AcceptServiceEnrollmentCommand(15L,999L));
        assertTrue(result.isFailure());
        verify(enrollments,never()).save(any());
    }
}
