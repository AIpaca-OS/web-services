package com.aipaca.rumbo.platform.notifications.domain.model.valueobjects;

import java.util.UUID;

/** Routes identifies routes using UUIDs, not course IDs. */
public record RouteId(UUID routeId) {
    public RouteId {
        if (routeId == null) throw new IllegalArgumentException("Route UUID required");
    }
}
