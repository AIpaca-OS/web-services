package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteResource;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteScheduleResource;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteStopResource;

import java.util.Objects;

/**
 * Assembler to convert a Route entity to a RouteResource.
 */
public class RouteResourceFromEntityAssembler {
    /**
     * Converts a Route entity to a RouteResource.
     *
     * @param entity The {@link Route} entity to convert.
     * @return The {@link RouteResource} resource.
     */
    public static RouteResource toResourceFromEntity(Route entity) {
        var stops = entity.getStops().stream()
                .map(stop -> new RouteStopResource(stop.getId(), stop.getAddress(), stop.getSchoolIdValue(), stop.getPosition(),
                        entity.getAssignments().stream()
                                .filter(assignment -> Objects.equals(assignment.getStopId(), stop.getId()))
                                .map(RouteAssignment::getStudentIdValue)
                                .toList()))
                .toList();
        var schedule = entity.getSchedule().stream()
                .map(row -> new RouteScheduleResource(row.dayOfWeek(), row.departureTime().toString()))
                .toList();
        return new RouteResource(
                entity.getId(),
                entity.getDriverProfileIdValue(),
                entity.getName(),
                entity.getCode(),
                entity.getDepartureTime() == null ? "" : entity.getDepartureTime().toString(),
                entity.getStatus(),
                entity.getShift(),
                entity.getVehicleIdValue(),
                stops.size(),
                stops,
                schedule,
                entity.isScheduleConfirmed());
    }
}
