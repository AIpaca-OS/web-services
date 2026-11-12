package com.aipaca.rumbo.platform.routes.domain.model.commands;

/**
 * Delete Route Command
 *
 * @param routeId route id
 */
public record DeleteRouteCommand(Long routeId) {
    public DeleteRouteCommand {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
    }
}
