package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ServiceEnrollmentStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ServiceEnrollmentPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.ServiceEnrollmentPersistenceRepository;
import org.hibernate.mapping.Column;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import java.sql.Types;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ServiceEnrollmentPersistenceMappingTests extends OfflineProfilesMappingTestSupport {
    private java.util.Map<String, Column> columns() {
        return metadata.getEntityBinding(ServiceEnrollmentPersistenceEntity.class.getName()).getTable()
                .getColumns().stream().collect(Collectors.toMap(Column::getName, Function.identity()));
    }

    @Test
    void mappingPreservesAllColumnsAndNullableTransitionDates() {
        var table = metadata.getEntityBinding(ServiceEnrollmentPersistenceEntity.class.getName()).getTable();
        assertEquals("service_enrollments", table.getName());
        var columns = columns();
        assertEquals(Set.of("id", "student_id", "driver_profile_id", "requested_by_profile_id", "status",
                "requested_at", "accepted_at", "cancelled_at", "ended_at", "created_at", "updated_at"), columns.keySet());
        for (var column : columns.values()) {
            assertEquals(Set.of("accepted_at", "cancelled_at", "ended_at").contains(column.getName()), column.isNullable(),
                    column.getName());
        }
        for (String name : List.of("id", "student_id", "driver_profile_id", "requested_by_profile_id")) {
            assertEquals(Types.BIGINT, columns.get(name).getSqlTypeCode(metadata), name);
        }
        for (String name : List.of("requested_at", "accepted_at", "cancelled_at", "ended_at")) {
            assertEquals(SqlTypes.TIMESTAMP_UTC, columns.get(name).getSqlTypeCode(metadata), name);
            assertTrue(columns.get(name).getSqlType(metadata).contains("with time zone"), name);
        }
    }

    @Test
    void statusIsStringAndNotAnEnumOrdinal() {
        var status = columns().get("status");
        assertEquals(Types.VARCHAR, status.getSqlTypeCode(metadata));
        assertEquals(10L, status.getLength());
        assertTrue(allowedStatuses().stream().allMatch(v -> v.length() <= 10));
    }

    private static List<String> allowedStatuses() {
        return java.util.Arrays.stream(ServiceEnrollmentStatus.values()).map(Enum::name).toList();
    }

    @Test
    void noUnconditionalPairUniquenessBlocksNewEnrollmentAfterEnded() {
        var table = metadata.getEntityBinding(ServiceEnrollmentPersistenceEntity.class.getName()).getTable();
        assertTrue(table.getUniqueKeys().isEmpty());
    }

    @Test
    void springDataCanBuildDerivedQueriesIncludingActivePairStatusFilterOffline() {
        try (var entityManager = sessionFactory.createEntityManager()) {
            var repository = new JpaRepositoryFactory(entityManager)
                    .getRepository(ServiceEnrollmentPersistenceRepository.class);
            assertNotNull(repository);
        }
    }
}
