package com.aipaca.rumbo.platform.notifications.domain.model.valueobjects;

/**
 * Value object representing the route id.
 *
 * <p>Links a notification to a route of the Routes bounded context.</p>
 *
 * @param routeId The route id. It cannot be null or less than 1.
 */
public record RouteId(Long routeId) {
    public RouteId {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
    }
}
