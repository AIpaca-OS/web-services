package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.DriverProfileId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link DriverProfileId} values between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class DriverProfileIdPersistenceConverter implements AttributeConverter<DriverProfileId, Long> {

    @Override
    public Long convertToDatabaseColumn(DriverProfileId attribute) {
        return attribute == null ? null : attribute.driverProfileId();
    }

    @Override
    public DriverProfileId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new DriverProfileId(dbData);
    }
}
