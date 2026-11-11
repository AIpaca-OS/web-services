package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import org.hibernate.mapping.Column;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.jpa.repository.Query;

import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Bootstraps only Hibernate metadata and query parsing; any JDBC connection attempt fails. */
class ProfilePersistenceMappingTests extends OfflineProfilesMappingTestSupport {

    @Test
    void accountIsANonNullableBigintWithANamedUniqueConstraint() {
        var table = metadata.getEntityBinding(ProfilePersistenceEntity.class.getName()).getTable();
        assertEquals("profiles", table.getName());
        var account = table.getColumns().stream().filter(c -> c.getName().equals("account_id"))
                .findFirst().orElseThrow();
        assertFalse(account.isNullable());
        assertEquals(Types.BIGINT, account.getSqlTypeCode(metadata));
        var unique = table.getUniqueKey("uk_profiles_account_id");
        assertNotNull(unique);
        assertEquals(List.of("account_id"), unique.getColumns().stream().map(Column::getName).toList());
    }

    @Test
    void mappingHasExpectedColumnsAndReportLengthsWithoutRemovedAttributes() {
        var columns = metadata.getEntityBinding(ProfilePersistenceEntity.class.getName()).getTable().getColumns();
        assertEquals(8, columns.size());
        for (var column : columns) {
            assertFalse(column.isNullable(), column.getName());
            switch (column.getName()) {
                case "first_name", "last_name" -> assertEquals(100L, column.getLength());
                case "phone_number" -> assertEquals(20L, column.getLength());
                case "profile_type" -> assertEquals(10L, column.getLength());
                case "id", "account_id", "created_at", "updated_at" -> { }
                default -> fail("Unexpected column: " + column.getName());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"findByAccountId", "countByAccountId"})
    void accountQueriesAreValidForTheConvertedValueObject(String methodName) throws Exception {
        var method = ProfilePersistenceRepository.class.getMethod(methodName, AccountId.class);
        var hql = method.getAnnotation(Query.class).value();
        try (var entityManager = sessionFactory.createEntityManager()) {
            var query = entityManager.createQuery(hql, Object.class);
            query.setParameter("accountId", new AccountId(101L));
            assertEquals(AccountId.class, query.getParameter("accountId").getParameterType());
        }
    }
}
