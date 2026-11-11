package com.aipaca.rumbo.notifications.domain.model.valueobjects;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RouteId(Long routeId) {

    public RouteId {
        if (routeId == null || routeId < 1) throw new IllegalArgumentException("Route id cannot be null or less than 1");
    }
}
