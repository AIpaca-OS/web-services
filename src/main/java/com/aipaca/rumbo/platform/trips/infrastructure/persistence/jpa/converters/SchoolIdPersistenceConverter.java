package com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.SchoolId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link SchoolId} values between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class SchoolIdPersistenceConverter implements AttributeConverter<SchoolId, Long> {

    @Override
    public Long convertToDatabaseColumn(SchoolId attribute) {
        return attribute == null ? null : attribute.schoolId();
    }

    @Override
    public SchoolId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new SchoolId(dbData);
    }
}
