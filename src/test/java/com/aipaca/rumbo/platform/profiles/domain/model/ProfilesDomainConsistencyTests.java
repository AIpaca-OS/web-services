package com.aipaca.rumbo.platform.profiles.domain.model;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByAccountIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProfilesDomainConsistencyTests {
    private static final Instant NOW = Instant.parse("2026-11-10T12:00:00Z");
    private Student registered() {
        return new Student(new RegisterStudentCommand(1L, "Ana", "Perez",
                LocalDate.of(2016, 3, 10), 2L, RelationshipType.MOTHER), NOW);
    }

    @Test void accountQueryRejectsInvalidIdentity() {
        assertThrows(IllegalArgumentException.class, () -> new GetProfileByAccountIdQuery(null));
        assertThrows(IllegalArgumentException.class, () -> new GetProfileByAccountIdQuery(0L));
        assertEquals(1L, new GetProfileByAccountIdQuery(1L).accountId());
    }

    @Test void registrationCreatesFirstAuthorizationForRegisteringTutor() {
        var student = registered();
        assertEquals(1, student.getTutorRelationships().size());
        var initial = student.getTutorRelationships().getFirst();
        assertNull(initial.getId());
        assertEquals(1L, initial.getTutorProfileId());
        assertEquals(1L, initial.getAuthorizedByProfileId());
        assertTrue(initial.isAuthorized());
        assertThrows(UnsupportedOperationException.class, () -> student.getTutorRelationships().clear());
    }

    @Test void failedStudentUpdatePreservesEarlierFields() {
        var student = registered();
        assertThrows(NullPointerException.class, () -> student.updateInformation("Eva", "Lopez", null, 3L));
        assertEquals("Ana Perez", student.getFullName());
        assertEquals(2L, student.getSchoolId());
    }

    @Test void rejectsUnauthorizedAuthorizerAndDuplicateActiveTutor() {
        var student = registered();
        assertThrows(IllegalStateException.class, () -> student.authorizeTutor(2L, RelationshipType.FATHER, 77L, NOW));
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, NOW.plusSeconds(1));
        assertThrows(IllegalStateException.class, () -> student.authorizeTutor(2L, RelationshipType.FATHER, 1L, NOW.plusSeconds(2)));
        assertEquals(2, student.getTutorRelationships().size());
    }

    @Test void refusesRevokingTheLastTutorAndUnauthorizedRevoker() {
        var student = registered();
        assertThrows(IllegalStateException.class, () -> student.revokeTutor(1L, 1L, NOW.plusSeconds(1)));
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, NOW.plusSeconds(1));
        assertThrows(IllegalStateException.class, () -> student.revokeTutor(2L, 99L, NOW.plusSeconds(2)));
        student.revokeTutor(2L, 1L, NOW.plusSeconds(2));
        assertFalse(student.hasAuthorizedTutor(2L));
        assertEquals(1, student.getTutorRelationships().stream().filter(TutorStudentRelationship::isAuthorized).count());
    }

    @Test void reauthorizationCreatesNewHistoryRatherThanReusingRevokedEntity() {
        var student = registered();
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, NOW.plusSeconds(1));
        student.revokeTutor(2L, 1L, NOW.plusSeconds(2));
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, NOW.plusSeconds(3));
        assertEquals(3, student.getTutorRelationships().size());
        assertFalse(student.getTutorRelationships().get(1).isAuthorized());
        assertTrue(student.getTutorRelationships().get(2).isAuthorized());
        assertNotSame(student.getTutorRelationships().get(1), student.getTutorRelationships().get(2));
    }

    @Test void reconstitutionRejectsBadHistoricalData() {
        assertThrows(IllegalArgumentException.class, () -> TutorStudentRelationship.reconstitute(
                1L, 2L, 1L, RelationshipType.FATHER, AuthorizationStatus.REVOKED, NOW, NOW.minusSeconds(1)));
        var rel = TutorStudentRelationship.reconstitute(1L, 2L, 1L, RelationshipType.FATHER,
                AuthorizationStatus.REVOKED, NOW, NOW.plusSeconds(1));
        assertThrows(IllegalArgumentException.class, () -> Student.reconstitute(1L,
                new com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName("Ana", "Perez"),
                LocalDate.of(2016, 3, 10), 2L,
                com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus.ACTIVE,
                null, 0L, 0L, List.of(rel)));
    }

    @Test void lifecycleAnonymizationRevokesAllAndPreventsReactivation() {
        var student = registered();
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, NOW.plusSeconds(1));
        student.anonymize(NOW.plusSeconds(3));
        assertEquals(0, student.getTutorRelationships().stream().filter(TutorStudentRelationship::isAuthorized).count());
        assertEquals(com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus.INACTIVE, student.getStatus());
        assertThrows(IllegalStateException.class, student::activate);
    }

    @Test void authorizationRevisionChangesOnlyAfterSuccessfulMutations() {
        var student = registered();
        assertEquals(0L, student.getAuthorizationRevision());
        assertThrows(IllegalStateException.class,
                () -> student.revokeTutor(1L, 1L, NOW.plusSeconds(1)));
        assertEquals(0L, student.getAuthorizationRevision());
        student.authorizeTutor(2L, RelationshipType.FATHER, 1L, NOW.plusSeconds(1));
        assertEquals(1L, student.getAuthorizationRevision());
        student.revokeTutor(2L, 1L, NOW.plusSeconds(2));
        assertEquals(2L, student.getAuthorizationRevision());
        student.anonymize(NOW.plusSeconds(3));
        assertEquals(3L, student.getAuthorizationRevision());
    }
}
