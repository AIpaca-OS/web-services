package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class TutorStudentRelationshipPersistenceAssemblerTests {
    private static final Instant AT = Instant.parse("2026-11-10T12:00:00Z");

    @Test void retainsHistoryAndOriginalAuthorizer() {
        var original = TutorStudentRelationship.reconstitute(5L, 2L, 1L,
                RelationshipType.FATHER, AuthorizationStatus.REVOKED, AT, AT.plusSeconds(15));
        var restored = TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(original));
        assertEquals(5L, restored.getId());
        assertEquals(2L, restored.getTutorProfileId());
        assertEquals(1L, restored.getAuthorizedByProfileId());
        assertFalse(restored.isAuthorized());
        assertEquals(AT.plusSeconds(15), restored.getRevokedAt());
    }

    @Test void corruptedChronologyIsRejected() {
        var original = TutorStudentRelationship.reconstitute(5L, 2L, 1L,
                RelationshipType.FATHER, AuthorizationStatus.AUTHORIZED, AT, null);
        var stored = TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(original);
        stored.setAuthorizationStatus(AuthorizationStatus.REVOKED);
        stored.setRevokedAt(AT.minusSeconds(1));
        assertThrows(IllegalArgumentException.class, () ->
                TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(stored));
    }
}
