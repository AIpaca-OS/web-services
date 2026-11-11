package com.aipaca.rumbo.platform.profiles.domain.model;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByAccountIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProfilesDomainConsistencyTests {
    @Test
    void accountQueryRejectsInvalidIdentity() {
        assertThrows(IllegalArgumentException.class, () -> new GetProfileByAccountIdQuery(null));
        assertThrows(IllegalArgumentException.class, () -> new GetProfileByAccountIdQuery(0L));
        assertThrows(IllegalArgumentException.class, () -> new GetProfileByAccountIdQuery(-1L));
        assertEquals(1L, new GetProfileByAccountIdQuery(1L).accountId());
    }

    @Test
    void failedStudentUpdatePreservesAllPreviousInformation() {
        var birthDate = LocalDate.of(2016, 3, 10);
        var student = new Student(new RegisterStudentCommand(
                1L, "Ana", "Perez", birthDate, 2L, RelationshipType.MOTHER));

        assertThrows(NullPointerException.class,
                () -> student.updateInformation("Eva", "Lopez", null, 3L));

        assertEquals("Ana Perez", student.getFullName());
        assertEquals(birthDate, student.getBirthDate());
        assertEquals(2L, student.getSchoolId());
    }

    @Test
    void reconstitutionRejectsRevocationBeforeAuthorization() {
        var at = Instant.parse("2026-11-10T12:00:00Z");
        assertThrows(IllegalArgumentException.class, () -> TutorStudentRelationship.reconstitute(
                1L, 2L, 3L, 4L, RelationshipType.FATHER,
                AuthorizationStatus.REVOKED, at, at.minusSeconds(1)));
    }

    @Test
    void revocationRemovesAuthorizationAndReauthorizationRestoresIt() {
        var at = Instant.parse("2026-11-10T12:00:00Z");
        var relationship = new TutorStudentRelationship(new AuthorizeTutorStudentRelationshipCommand(
                3L, 2L, 4L, RelationshipType.FATHER), at);

        relationship.revoke(at.plusSeconds(1));
        assertFalse(relationship.isAuthorized());
        assertThrows(IllegalArgumentException.class, () -> relationship.authorize(at));
        assertFalse(relationship.isAuthorized());

        relationship.authorize(at.plusSeconds(2));
        assertTrue(relationship.isAuthorized());
        assertNull(relationship.getRevokedAt());
    }
}
