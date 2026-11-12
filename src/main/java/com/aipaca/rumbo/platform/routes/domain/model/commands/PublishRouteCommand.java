package com.aipaca.rumbo.platform.routes.domain.model.commands;

/**
 * Publish Route Command
 *
 * @param routeId route id
 */
public record PublishRouteCommand(Long routeId) {
    public PublishRouteCommand {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
    }
}
