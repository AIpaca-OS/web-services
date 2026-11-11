package com.aipaca.rumbo.platform.routes.domain.model.commands;
    import com.aipaca.rumbo.platform.routes.domain.model.RouteSchedule;
    import com.aipaca.rumbo.platform.routes.domain.model.RouteShift;
    import com.aipaca.rumbo.platform.routes.domain.model.RouteStop;

    import java.util.*;

/** Domain intent. Validation and business rules live in the aggregate. */
    public record CreateRouteCommand(String name, RouteShift shift, String vehicleId, List<RouteStop> stops, List<RouteSchedule> schedule, boolean scheduleConfirmed) {}
