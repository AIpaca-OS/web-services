package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Converts the external IAM account reference to a numeric column. */
@Converter(autoApply = false)
public class AccountIdPersistenceConverter implements AttributeConverter<AccountId, Long> {
    @Override
    public Long convertToDatabaseColumn(AccountId attribute) {
        return attribute == null ? null : attribute.accountId();
    }

    @Override
    public AccountId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new AccountId(dbData);
    }
}
