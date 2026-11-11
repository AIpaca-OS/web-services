package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.converters;

import com.aipaca.rumbo.notifications.domain.model.valueobjects.RouteId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = false)
public class RouteIdPersistenceConverter implements AttributeConverter<RouteId, UUID> {
    @Override public UUID convertToDatabaseColumn(RouteId value) {
        return value == null ? null : value.routeId();
    }
    @Override public RouteId convertToEntityAttribute(UUID value) {
        return value == null ? null : new RouteId(value);
    }
}
