package com.aipaca.rumbo.platform.trips.domain.model.valueobjects;

/**
 * Value object representing the route id.
 *
 * <p>Links a trip to the route of the Routes bounded context it was planned from.</p>
 *
 * @param routeId The route id. It cannot be null or less than 1.
 */
public record RouteId(Long routeId) {
    /**
     * Compact constructor for RouteId.
     *
     * @throws IllegalArgumentException if the route id is null or less than 1.
     */
    public RouteId {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
    }
}
