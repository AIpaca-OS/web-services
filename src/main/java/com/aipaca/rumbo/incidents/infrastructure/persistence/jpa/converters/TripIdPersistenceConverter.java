package com.aipaca.rumbo.incidents.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link TripId} values between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class TripIdPersistenceConverter implements AttributeConverter<TripId, Long> {

    @Override
    public Long convertToDatabaseColumn(TripId attribute) {
        return attribute == null ? null : attribute.tripId();
    }

    @Override
    public TripId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new TripId(dbData);
    }
}
