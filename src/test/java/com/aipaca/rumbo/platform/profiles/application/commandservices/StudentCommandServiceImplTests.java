package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.application.events.ProfilesEventPublisher;
import com.aipaca.rumbo.platform.profiles.application.internal.commandservices.StudentCommandServiceImpl;
import com.aipaca.rumbo.platform.profiles.application.security.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.events.TutorAuthorized;
import com.aipaca.rumbo.platform.profiles.domain.model.events.TutorAuthorizationRevoked;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentCommandServiceImplTests {
    @Mock StudentRepository students;
    @Mock SchoolRepository schools;
    @Mock ProfileRepository profiles;
    @Mock ProfilesAuthorization auth;
    @Mock ProfilesEventPublisher events;
    StudentCommandServiceImpl service;
    static final Instant AT = Instant.parse("2026-11-10T11:00:00Z");
    static final LocalDate DOB = LocalDate.of(2016, 5, 3);

    @BeforeEach void setup() { service=new StudentCommandServiceImpl(students, schools, profiles, auth, events); }

    private Profile tutor() {
        return Profile.reconstitute(10L, new AccountId(3L), ProfileType.TUTOR,
                new PersonName("María", "Pérez"), "999999999");
    }
    private Student existing() {
        var rel=TutorStudentRelationship.reconstitute(31L, 10L, 10L,
                RelationshipType.MOTHER, AuthorizationStatus.AUTHORIZED, AT, null);
        return Student.reconstitute(25L, new PersonName("Ana", "Pérez"), DOB,
                null, StudentStatus.ACTIVE, null, 0L, 0L, List.of(rel));
    }
    private Student assignedIdentity(Student original) {
        var source = original.getTutorRelationships().getFirst();
        var rel=TutorStudentRelationship.reconstitute(31L, source.getTutorProfileId(),
                source.getAuthorizedByProfileId(),source.getRelationshipType(),
                source.getAuthorizationStatus(),source.getAuthorizedAt(),source.getRevokedAt());
        return Student.reconstitute(25L,original.getName(),original.getBirthDate(),
                original.getSchoolId(),original.getStatus(),original.getAnonymizedAt(),
                0L, original.getAuthorizationRevision(),List.of(rel));
    }

    @Test void registerStudentCreatesInitialAuthorizationAndEmitsEvent() {
        when(auth.currentTutor()).thenReturn(tutor());
        when(students.save(any(Student.class))).thenAnswer(call -> assignedIdentity(call.getArgument(0)));
        var result=service.handle(new RegisterStudentCommand(10L,"Ana","Pérez",DOB,null,RelationshipType.MOTHER));
        assertTrue(result.isSuccess());
        assertEquals(25L,result.toOptional().orElseThrow());
        verify(events).publishAfterCommit(any(TutorAuthorized.class));
        verify(students).save(argThat(s -> s.hasAuthorizedTutor(10L)));
    }

    @Test void foreignTutorCannotRegisterOrPersistStudent() {
        when(auth.currentTutor()).thenReturn(tutor());
        var result=service.handle(new RegisterStudentCommand(99L,"Ana","Pérez",DOB,null,RelationshipType.MOTHER));
        assertTrue(result.isFailure());
        verifyNoInteractions(students,events);
    }

    @Test void cannotRevokeTheLastAuthorizedTutor() {
        when(auth.currentTutor()).thenReturn(tutor());
        when(auth.requireStudentAccess(25L)).thenReturn(existing());
        var result=service.handle(new RevokeTutorStudentRelationshipCommand(25L,10L,10L));
        assertTrue(result.isFailure());
        verify(students,never()).save(any());
        verifyNoInteractions(events);
    }

    @Test void authorizedTutorCanGrantAnotherTutorAndDispatchEvent() {
        when(auth.currentTutor()).thenReturn(tutor());
        when(auth.requireStudentAccess(25L)).thenReturn(existing());
        when(profiles.findById(11L)).thenReturn(java.util.Optional.of(Profile.reconstitute(
                11L, new AccountId(4L), ProfileType.TUTOR,
                new PersonName("Pedro", "Pérez"), "988888888")));
        when(students.save(any(Student.class))).thenAnswer(call -> call.getArgument(0));
        var result=service.handle(new AuthorizeTutorStudentRelationshipCommand(
                25L,11L,10L,RelationshipType.FATHER));
        assertTrue(result.isSuccess());
        verify(students).save(argThat(s -> s.hasAuthorizedTutor(11L)));
        verify(events).publishAfterCommit(any(TutorAuthorized.class));
    }
}
