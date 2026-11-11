package com.aipaca.rumbo.platform.routes.domain.model.commands;

import com.aipaca.rumbo.platform.routes.domain.model.RouteSchedule;
import com.aipaca.rumbo.platform.routes.domain.model.RouteShift;
import com.aipaca.rumbo.platform.routes.domain.model.RouteStop;

import java.util.*;

/** Changes an existing route. Published changes require verified driver permissions. */
public record UpdateRouteCommand(
        UUID routeId, String name, RouteShift shift, String vehicleId,
        List<RouteStop> stops, List<RouteSchedule> schedule,
        boolean scheduleConfirmed, String authorizationHeader) {
    public UpdateRouteCommand(UUID routeId, String name, RouteShift shift, String vehicleId,
            List<RouteStop> stops, List<RouteSchedule> schedule, boolean scheduleConfirmed) {
        this(routeId, name, shift, vehicleId, stops, schedule, scheduleConfirmed, null);
    }
}
