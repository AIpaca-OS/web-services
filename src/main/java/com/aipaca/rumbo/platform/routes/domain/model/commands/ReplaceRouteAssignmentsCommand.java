package com.aipaca.rumbo.platform.routes.domain.model.commands;

import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;

import java.util.List;

/**
 * Replace Route Assignments Command
 *
 * @param routeId     route id
 * @param assignments complete list of student-to-stop assignments
 */
public record ReplaceRouteAssignmentsCommand(Long routeId, List<RouteAssignment> assignments) {
    public ReplaceRouteAssignmentsCommand {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
        if (assignments == null) {
            throw new IllegalArgumentException("Assignments cannot be null");
        }
        assignments = List.copyOf(assignments);
    }
}
