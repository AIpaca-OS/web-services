package com.aipaca.rumbo.routes.domain.model.commands;

import java.util.UUID;

/** Revalidate every student with Profiles before publishing a route. */
public record PublishRouteCommand(UUID routeId, String authorizationHeader) {}
