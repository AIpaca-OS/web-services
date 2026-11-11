package com.aipaca.rumbo.platform.routes.domain.model.commands;

import com.aipaca.rumbo.platform.routes.domain.model.RouteSchedule;
import java.util.List;
import java.util.UUID;

/** Change a weekly schedule; published routes require verified driver access. */
public record UpdateRouteScheduleCommand(
        UUID routeId, List<RouteSchedule> schedule, boolean confirm, String authorizationHeader) {
    public UpdateRouteScheduleCommand(UUID routeId, List<RouteSchedule> schedule, boolean confirm) {
        this(routeId, schedule, confirm, null);
    }
}
