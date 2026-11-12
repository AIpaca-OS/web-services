package com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.RouteId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link RouteId} values between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class RouteIdPersistenceConverter implements AttributeConverter<RouteId, Long> {

    @Override
    public Long convertToDatabaseColumn(RouteId attribute) {
        return attribute == null ? null : attribute.routeId();
    }

    @Override
    public RouteId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new RouteId(dbData);
    }
}
