package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.StudentPersistenceEntity;
import org.hibernate.mapping.Column;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class StudentPersistenceMappingTests extends OfflineProfilesMappingTestSupport {
    private Map<String, Column> columns() {
        return metadata.getEntityBinding(StudentPersistenceEntity.class.getName()).getTable().getColumns()
                .stream().collect(Collectors.toMap(Column::getName, Function.identity()));
    }

    @Test
    void studentMappingHasExpectedColumnsAndNullabilityWithoutEducationLevel() {
        var table = metadata.getEntityBinding(StudentPersistenceEntity.class.getName()).getTable();
        assertEquals("students", table.getName());
        var columns = columns();
        assertEquals(Set.of("id", "first_name", "last_name", "birth_date", "school_id",
                "status", "anonymized_at", "created_at", "updated_at"), columns.keySet());
        for (var column : columns.values()) {
            assertEquals(Set.of("school_id", "anonymized_at").contains(column.getName()), column.isNullable(),
                    column.getName());
        }
        assertTrue(table.getUniqueKeys().isEmpty());
    }

    @Test
    void datesSchoolReferenceAndStatusUseNativeMappings() {
        var columns = columns();
        assertEquals(Types.DATE, columns.get("birth_date").getSqlTypeCode(metadata));
        assertEquals(Types.BIGINT, columns.get("school_id").getSqlTypeCode(metadata));
        assertEquals(SqlTypes.TIMESTAMP_UTC, columns.get("anonymized_at").getSqlTypeCode(metadata));
        assertTrue(columns.get("anonymized_at").getSqlType(metadata).contains("with time zone"));
        assertEquals(Types.VARCHAR, columns.get("status").getSqlTypeCode(metadata));
        assertEquals(10L, columns.get("status").getLength());
        assertTrue(metadata.getEntityBinding(StudentPersistenceEntity.class.getName()).getTable()
                .getForeignKeys().isEmpty());
    }

    @Test
    void reusedNameEmbeddableHasIndependentColumnsInBothTables() {
        var studentColumns = columns();
        var profileTable = metadata.getEntityBinding(ProfilePersistenceEntity.class.getName()).getTable();
        for (String name : Set.of("first_name", "last_name")) {
            var profileColumn = profileTable.getColumns().stream().filter(c -> c.getName().equals(name))
                    .findFirst().orElseThrow();
            assertEquals(100L, studentColumns.get(name).getLength());
            assertEquals(100L, profileColumn.getLength());
            assertNotSame(profileColumn, studentColumns.get(name));
        }
    }
}
