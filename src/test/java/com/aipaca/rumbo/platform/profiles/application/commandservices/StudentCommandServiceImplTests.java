package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.application.internal.commandservices.StudentCommandServiceImpl;
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
class StudentCommandServiceImplTests {
    @Mock StudentRepository students;
    @Mock ProfileRepository profiles;
    StudentCommandServiceImpl service;
    static final Instant AT = Instant.parse("2026-11-10T11:00:00Z");
    static final LocalDate DOB = LocalDate.of(2016, 5, 3);

    @BeforeEach void setup() {
        service = new StudentCommandServiceImpl(students, profiles);
    }

    private Profile tutor(Long id) {
        return Profile.reconstitute(id, new AccountId(id + 100), ProfileType.TUTOR,
                new PersonName("Tutor", "Pérez"), "999999999");
    }

    private Student existing() {
        var relationship = TutorStudentRelationship.reconstitute(31L, 10L, 10L,
                RelationshipType.MOTHER, AuthorizationStatus.AUTHORIZED, AT, null);
        return Student.reconstitute(25L, new PersonName("Ana", "Pérez"), DOB, StudentStatus.ACTIVE, null, 0L, 0L, List.of(relationship));
    }

    private Student assignedIdentity(Student original) {
        var first = original.getTutorRelationships().getFirst();
        var relationship = TutorStudentRelationship.reconstitute(31L, first.getTutorProfileId(),
                first.getAuthorizedByProfileId(), first.getRelationshipType(),
                first.getAuthorizationStatus(), first.getAuthorizedAt(), first.getRevokedAt());
        return Student.reconstitute(25L, original.getName(), original.getBirthDate(), original.getStatus(), original.getAnonymizedAt(), 0L,
                original.getAuthorizationRevision(), List.of(relationship));
    }

    @Test void registerStudentCreatesFirstAuthorizedTutor() {
        when(profiles.findById(10L)).thenReturn(Optional.of(tutor(10L)));
        when(students.save(any(Student.class))).thenAnswer(call -> assignedIdentity(call.getArgument(0)));
        var result = service.handle(new RegisterStudentCommand(10L,"Ana","Pérez",DOB,RelationshipType.MOTHER));
        assertTrue(result.isSuccess());
        assertEquals(25L, result.toOptional().orElseThrow());
        verify(students).save(argThat(student -> student.hasAuthorizedTutor(10L)));
    }

    @Test void nonExistingTutorCannotRegisterStudent() {
        when(profiles.findById(99L)).thenReturn(Optional.empty());
        var result = service.handle(new RegisterStudentCommand(99L,"Ana","Pérez",DOB,RelationshipType.MOTHER));
        assertTrue(result.isFailure());
        verify(students, never()).save(any());
    }

    @Test void cannotRevokeLastAuthorizedTutor() {
        when(students.findById(25L)).thenReturn(Optional.of(existing()));
        var result = service.handle(new RevokeTutorStudentRelationshipCommand(25L,10L,10L));
        assertTrue(result.isFailure());
        verify(students, never()).save(any());
    }

    @Test void existingAuthorizedTutorCanAddAnotherTutor() {
        when(students.findById(25L)).thenReturn(Optional.of(existing()));
        when(profiles.findById(10L)).thenReturn(Optional.of(tutor(10L)));
        when(profiles.findById(11L)).thenReturn(Optional.of(tutor(11L)));
        when(students.save(any(Student.class))).thenAnswer(call -> call.getArgument(0));
        var result = service.handle(new AuthorizeTutorStudentRelationshipCommand(
                25L,11L,10L,RelationshipType.FATHER));
        assertTrue(result.isSuccess());
        verify(students).save(argThat(student -> student.hasAuthorizedTutor(11L)));
    }
}
