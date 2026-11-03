package com.aipaca.rumbo.routes.domain.model.commands;
    import com.aipaca.rumbo.routes.domain.model.*;
import java.util.*;
import java.time.*;
    /** Domain intent. Validation and business rules live in the aggregate. */
    public record UpdateRouteCommand(UUID routeId, String name, RouteShift shift, String vehicleId, List<RouteStop> stops, List<RouteSchedule> schedule, boolean scheduleConfirmed) {}
