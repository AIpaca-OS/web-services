package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TutorStudentRelationshipPersistenceAssemblerTests {
    private static final Instant AUTHORIZED_AT = Instant.parse("2026-11-10T12:00:00Z");
    private static final Instant REVOKED_AT = AUTHORIZED_AT.plusSeconds(60);

    private TutorStudentRelationship existingRelationship(RelationshipType type) {
        return TutorStudentRelationship.reconstitute(1L, 2L, 3L, 4L, type,
                AuthorizationStatus.AUTHORIZED, AUTHORIZED_AT, null);
    }

    private void assertEquivalent(TutorStudentRelationship expected, TutorStudentRelationship actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTutorProfileId(), actual.getTutorProfileId());
        assertEquals(expected.getStudentId(), actual.getStudentId());
        assertEquals(expected.getAuthorizedByProfileId(), actual.getAuthorizedByProfileId());
        assertEquals(expected.getRelationshipType(), actual.getRelationshipType());
        assertEquals(expected.getAuthorizationStatus(), actual.getAuthorizationStatus());
        assertEquals(expected.getAuthorizedAt(), actual.getAuthorizedAt());
        assertEquals(expected.getRevokedAt(), actual.getRevokedAt());
    }

    @Test
    void creationLeavesIdentityPendingAndKeepsTheProvidedAuthorizationDate() {
        var relationship = new TutorStudentRelationship(new AuthorizeTutorStudentRelationshipCommand(
                3L, 2L, 4L, RelationshipType.LEGAL_GUARDIAN), AUTHORIZED_AT);
        var entity = TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(relationship);
        assertNull(entity.getId());
        entity.setId(1L);
        var restored = TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(1L, restored.getId());
        assertEquals(2L, restored.getTutorProfileId());
        assertEquals(3L, restored.getStudentId());
        assertEquals(4L, restored.getAuthorizedByProfileId());
        assertEquals(AUTHORIZED_AT, restored.getAuthorizedAt());
        assertTrue(restored.isAuthorized());
        assertNull(relationship.getId());
    }

    @ParameterizedTest
    @EnumSource(RelationshipType.class)
    void reconstructionPreservesEveryRelationshipTypeAndBothAuthorizationStates(RelationshipType type) {
        var relationship = existingRelationship(type);
        assertEquivalent(relationship, TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(relationship)));
        relationship.revoke(REVOKED_AT);
        var revoked = TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(relationship));
        assertEquivalent(relationship, revoked);
        assertFalse(revoked.isAuthorized());
        assertTrue(revoked.domainEvents().isEmpty());
    }

    @Test
    void nullableAuthorizerIsPreservedWithoutInventingAnActor() {
        var relationship = TutorStudentRelationship.reconstitute(1L, 2L, 3L, null, RelationshipType.MOTHER,
                AuthorizationStatus.AUTHORIZED, AUTHORIZED_AT, null);
        var restored = TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(relationship));
        assertEquivalent(relationship, restored);
    }

    @Test
    void inconsistentStoredChronologyCannotBypassDomainValidation() {
        var entity = TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(
                existingRelationship(RelationshipType.FATHER));
        entity.setAuthorizationStatus(AuthorizationStatus.REVOKED);
        entity.setRevokedAt(AUTHORIZED_AT.minusSeconds(1));
        assertThrows(IllegalArgumentException.class,
                () -> TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(entity));
    }

    @Test
    void reauthorizationReusesThePersistedIdentity() {
        var relationship = existingRelationship(RelationshipType.MOTHER);
        relationship.revoke(REVOKED_AT);
        var restored = TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(relationship));
        restored.authorize(REVOKED_AT.plusSeconds(60));
        var reauthorized = TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(restored));
        assertEquivalent(restored, reauthorized);
        assertEquals(1L, reauthorized.getId());
        assertNull(reauthorized.getRevokedAt());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void reconstitutionRequiresAValidPersistedIdentity(Long id) {
        var entity = TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(
                existingRelationship(RelationshipType.OTHER));
        entity.setId(id);
        assertThrows(IllegalArgumentException.class,
                () -> TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(entity));
    }
}
