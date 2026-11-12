package com.aipaca.rumbo.platform.trips.domain.model.queries;

/**
 * Get All Trips By Route ID Query
 *
 * @param routeId route id
 */
public record GetAllTripsByRouteIdQuery(Long routeId) {
    public GetAllTripsByRouteIdQuery {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
    }
}
