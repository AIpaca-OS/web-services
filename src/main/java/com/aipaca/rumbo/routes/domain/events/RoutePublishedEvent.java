package com.aipaca.rumbo.routes.domain.events;

import java.util.UUID;

/** Public route lifecycle fact; contains no student or guardian data. */
public record RoutePublishedEvent(UUID routeId) {}
