package com.aipaca.rumbo.platform.routes.domain.events;

import java.util.UUID;

/** Driver-reported route condition, without private student identifiers. */
public record DriverRouteNoticeEvent(UUID eventId, UUID routeId, String kind, String message) {}
