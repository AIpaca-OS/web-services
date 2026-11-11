package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.DataDeletionRequestPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.DataDeletionRequestPersistenceRepository;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class DataDeletionRequestPersistenceMappingTests extends OfflineProfilesMappingTestSupport {
    @Test void deletionRequestMappingMatchesDomainContract() {
        var table = metadata.getEntityBinding(DataDeletionRequestPersistenceEntity.class.getName()).getTable();
        assertEquals("data_deletion_requests", table.getName());
        var columns = table.getColumns().stream().collect(Collectors.toMap(c -> c.getName(), c -> c));
        assertEquals(Set.of("id", "student_id", "requested_by_profile_id", "status", "requested_at",
                "resolved_at", "resolution_note", "created_at", "updated_at"), columns.keySet());
        assertFalse(columns.get("requested_at").isNullable());
        assertTrue(columns.get("resolved_at").isNullable());
        assertTrue(columns.get("resolution_note").isNullable());
        assertEquals(500L, columns.get("resolution_note").getLength());
        assertEquals(SqlTypes.TIMESTAMP_UTC, columns.get("requested_at").getSqlTypeCode(metadata));
        assertEquals(SqlTypes.TIMESTAMP_UTC, columns.get("resolved_at").getSqlTypeCode(metadata));
    }
    @Test void derivedRepositoryMethodsBuildOffline() {
        try (var entityManager = sessionFactory.createEntityManager()) {
            assertNotNull(new JpaRepositoryFactory(entityManager).getRepository(
                    DataDeletionRequestPersistenceRepository.class));
        }
    }
}
