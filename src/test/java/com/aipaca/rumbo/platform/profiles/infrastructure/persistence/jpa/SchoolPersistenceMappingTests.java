package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.SchoolPersistenceEntity;
import org.hibernate.mapping.Column;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SchoolPersistenceMappingTests extends OfflineProfilesMappingTestSupport {
    @Test
    void schoolMappingUsesReportLengthsAndInheritedAuditColumns() {
        var table = metadata.getEntityBinding(SchoolPersistenceEntity.class.getName()).getTable();
        assertEquals("schools", table.getName());
        var columns = table.getColumns().stream()
                .collect(Collectors.toMap(Column::getName, Function.identity()));
        assertEquals(Set.of("id", "name", "address", "district", "created_at", "updated_at"), columns.keySet());
        assertTrue(columns.values().stream().noneMatch(Column::isNullable));
        for (var field : Map.of("name", 150L, "address", 255L, "district", 100L).entrySet()) {
            assertEquals(field.getValue(), columns.get(field.getKey()).getLength());
            assertEquals(Types.VARCHAR, columns.get(field.getKey()).getSqlTypeCode(metadata));
        }
    }

    @Test
    void schoolIdentityIsNumericWithoutInventingTextUniqueness() {
        var binding = metadata.getEntityBinding(SchoolPersistenceEntity.class.getName());
        var table = binding.getTable();
        var id = table.getColumns().stream().filter(column -> column.getName().equals("id"))
                .findFirst().orElseThrow();
        assertEquals(Types.BIGINT, id.getSqlTypeCode(metadata));
        assertEquals(Long.class, binding.getIdentifier().getType().getReturnedClass());
        assertEquals(Set.of("id"), table.getPrimaryKey().getColumns().stream()
                .map(Column::getName).collect(Collectors.toSet()));
        assertTrue(table.getUniqueKeys().isEmpty());
    }
}
