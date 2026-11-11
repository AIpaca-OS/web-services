package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.TutorStudentRelationshipPersistenceEntity;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class TutorStudentRelationshipPersistenceMappingTests extends OfflineProfilesMappingTestSupport {
    @Test void childEntityHasStudentForeignKeyAndNoHistoricalPairUniqueness() {
        var table = metadata.getEntityBinding(TutorStudentRelationshipPersistenceEntity.class.getName()).getTable();
        assertEquals("tutor_student_relationships", table.getName());
        var cols = table.getColumns().stream().map(c -> c.getName()).collect(Collectors.toSet());
        assertEquals(Set.of("id", "student_id", "tutor_profile_id", "authorized_by_profile_id",
                "relationship_type", "authorization_status", "authorized_at", "revoked_at",
                "created_at", "updated_at"), cols);
        assertTrue(table.getUniqueKeys().isEmpty(), "Revoked pairs must allow new historical rows");
        assertFalse(table.getForeignKeys().isEmpty(), "Child must reference Student root");
    }
}
