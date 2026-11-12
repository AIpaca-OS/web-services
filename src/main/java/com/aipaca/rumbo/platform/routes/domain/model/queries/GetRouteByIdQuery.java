package com.aipaca.rumbo.platform.routes.domain.model.queries;

/**
 * Get Route By ID Query
 *
 * @param routeId route id
 */
public record GetRouteByIdQuery(Long routeId) {
    public GetRouteByIdQuery {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
    }
}
