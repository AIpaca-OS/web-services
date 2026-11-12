package com.aipaca.rumbo.incidents.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ProfileId} values between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class ProfileIdPersistenceConverter implements AttributeConverter<ProfileId, Long> {

    @Override
    public Long convertToDatabaseColumn(ProfileId attribute) {
        return attribute == null ? null : attribute.profileId();
    }

    @Override
    public ProfileId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new ProfileId(dbData);
    }
}
