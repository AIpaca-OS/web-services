package com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.VehicleId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link VehicleId} values between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class VehicleIdPersistenceConverter implements AttributeConverter<VehicleId, Long> {

    @Override
    public Long convertToDatabaseColumn(VehicleId attribute) {
        return attribute == null ? null : attribute.vehicleId();
    }

    @Override
    public VehicleId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new VehicleId(dbData);
    }
}
