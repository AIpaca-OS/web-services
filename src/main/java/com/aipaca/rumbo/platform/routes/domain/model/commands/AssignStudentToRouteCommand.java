package com.aipaca.rumbo.platform.routes.domain.model.commands;

/**
 * Assign Student To Route Command
 *
 * @param routeId   route id
 * @param stopId    id of a stop of the route
 * @param studentId student id
 */
public record AssignStudentToRouteCommand(Long routeId, Long stopId, Long studentId) {
    public AssignStudentToRouteCommand {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
        if (stopId == null || stopId < 1) {
            throw new IllegalArgumentException("Stop id cannot be null or less than 1");
        }
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
