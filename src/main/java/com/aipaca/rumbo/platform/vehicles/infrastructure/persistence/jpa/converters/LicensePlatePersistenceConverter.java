package com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts license plates between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class LicensePlatePersistenceConverter implements AttributeConverter<LicensePlate, String> {

    @Override
    public String convertToDatabaseColumn(LicensePlate attribute) {
        return attribute == null ? null : attribute.plate();
    }

    @Override
    public LicensePlate convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new LicensePlate(dbData);
    }
}
