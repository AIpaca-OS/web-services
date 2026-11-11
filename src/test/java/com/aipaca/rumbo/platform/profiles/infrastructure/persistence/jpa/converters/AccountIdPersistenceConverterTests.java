package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AccountIdPersistenceConverterTests {
    private final AccountIdPersistenceConverter converter = new AccountIdPersistenceConverter();

    @ParameterizedTest
    @ValueSource(longs = {1L, 101L, Long.MAX_VALUE})
    void numericAccountIdentitySurvivesBothConversions(Long value) {
        var accountId = new AccountId(value);
        assertEquals(value, converter.convertToDatabaseColumn(accountId));
        assertEquals(accountId, converter.convertToEntityAttribute(value));
    }

    @Test
    void converterPreservesNullForJpaToHandleColumnNullability() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void invalidStoredAccountIdentityIsRejected(Long value) {
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(value));
    }
}
