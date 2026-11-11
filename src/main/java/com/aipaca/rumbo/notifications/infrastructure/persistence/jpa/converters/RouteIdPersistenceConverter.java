package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.converters;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.RouteId;


@Converter(autoApply = false)
public class RouteIdPersistenceConverter implements AttributeConverter<RouteId, Long> {

    @Override
    public Long convertToDatabaseColumn(RouteId attribute) {
        return attribute == null ? null : attribute.routeId();
    }

    @Override
    public RouteId convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new RouteId(dbData);
    }
}
