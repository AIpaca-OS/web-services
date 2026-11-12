package com.aipaca.rumbo.platform.routes.domain.model.commands;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteSchedule;

import java.util.List;

/**
 * Update Route Schedule Command
 *
 * @param routeId  route id
 * @param schedule weekly schedule
 * @param confirm  whether the schedule is confirmed
 */
public record UpdateRouteScheduleCommand(Long routeId, List<RouteSchedule> schedule, boolean confirm) {
    public UpdateRouteScheduleCommand {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }
        schedule = List.copyOf(schedule);
    }
}
