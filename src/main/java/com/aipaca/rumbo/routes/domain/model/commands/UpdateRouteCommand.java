package com.aipaca.rumbo.routes.domain.model.commands;

import com.aipaca.rumbo.routes.domain.model.*;
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
