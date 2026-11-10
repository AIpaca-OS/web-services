package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.TutorStudentRelationshipPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.TutorStudentRelationshipPersistenceRepository;
import org.hibernate.mapping.Column;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TutorStudentRelationshipPersistenceMappingTests extends OfflineProfilesMappingTestSupport {
    private Map<String, Column> columns() {
        return metadata.getEntityBinding(TutorStudentRelationshipPersistenceEntity.class.getName()).getTable()
                .getColumns().stream().collect(Collectors.toMap(Column::getName, Function.identity()));
    }

    @Test
    void mappingPreservesOptionalAuthorizerAndRevocationAndNumericReferences() {
        var table = metadata.getEntityBinding(TutorStudentRelationshipPersistenceEntity.class.getName()).getTable();
        assertEquals("tutor_student_relationships", table.getName());
        var columns = columns();
        assertEquals(Set.of("id", "tutor_profile_id", "student_id", "authorized_by_profile_id", "relationship_type",
                "authorization_status", "authorized_at", "revoked_at", "created_at", "updated_at"), columns.keySet());
        for (var column : columns.values()) {
            assertEquals(Set.of("authorized_by_profile_id", "revoked_at").contains(column.getName()), column.isNullable(),
                    column.getName());
        }
        for (String name : Set.of("id", "tutor_profile_id", "student_id", "authorized_by_profile_id")) {
            assertEquals(Types.BIGINT, columns.get(name).getSqlTypeCode(metadata));
        }
        for (String name : Set.of("authorized_at", "revoked_at")) {
            assertEquals(SqlTypes.TIMESTAMP_UTC, columns.get(name).getSqlTypeCode(metadata));
            assertTrue(columns.get(name).getSqlType(metadata).contains("with time zone"));
        }
    }

    @Test
    void enumColumnsStoreNamesAndFitAllCurrentValuesIncludingLegalGuardian() {
        var columns = columns();
        assertEquals(14L, columns.get("relationship_type").getLength());
        assertEquals(10L, columns.get("authorization_status").getLength());
        assertEquals(Types.VARCHAR, columns.get("relationship_type").getSqlTypeCode(metadata));
        assertEquals(Types.VARCHAR, columns.get("authorization_status").getSqlTypeCode(metadata));
        assertTrue(Arrays.stream(RelationshipType.values()).allMatch(v -> v.name().length() <= 14));
        assertTrue(Arrays.stream(AuthorizationStatus.values()).allMatch(v -> v.name().length() <= 10));
    }

    @Test
    void uniquenessAppliesToThePairAcrossAuthorizationStates() {
        var table = metadata.getEntityBinding(TutorStudentRelationshipPersistenceEntity.class.getName()).getTable();
        var constraint = table.getUniqueKey("uk_tutor_student_relationships_tutor_student");
        assertNotNull(constraint);
        assertEquals(List.of("tutor_profile_id", "student_id"),
                constraint.getColumns().stream().map(Column::getName).toList());
        assertEquals(1, table.getUniqueKeys().size());
    }

    @Test
    void springDataCanBuildTheRepositoryAndItsDerivedQueriesWithoutAConnection() {
        try (var entityManager = sessionFactory.createEntityManager()) {
            var repository = new JpaRepositoryFactory(entityManager)
                    .getRepository(TutorStudentRelationshipPersistenceRepository.class);
            assertNotNull(repository);
        }
    }
}
