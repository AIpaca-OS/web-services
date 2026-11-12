package com.aipaca.rumbo.platform.routes.domain.model.commands;

import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteSchedule;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteShift;

import java.util.List;

/**
 * Create Route Command
 *
 * @param driverProfileId   id of the driver profile responsible for the route
 * @param name              route name
 * @param shift             route shift
 * @param vehicleId         id of the vehicle assigned to the route, or null
 * @param stops             ordered new stops
 * @param schedule          weekly schedule
 * @param scheduleConfirmed whether the schedule is confirmed
 */
public record CreateRouteCommand(Long driverProfileId, String name, RouteShift shift, Long vehicleId,
                                 List<RouteStop> stops, List<RouteSchedule> schedule, boolean scheduleConfirmed) {
    public CreateRouteCommand {
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Driver profile id cannot be null or less than 1");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Route name cannot be null or blank");
        }
        if (shift == null) {
            throw new IllegalArgumentException("Route shift cannot be null");
        }
        stops = stops == null ? List.of() : List.copyOf(stops);
        schedule = schedule == null ? List.of() : List.copyOf(schedule);
    }
}
