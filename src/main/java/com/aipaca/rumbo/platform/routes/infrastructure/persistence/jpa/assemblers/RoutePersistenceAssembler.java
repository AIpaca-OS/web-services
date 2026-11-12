package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteSchedule;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.embeddables.RouteSchedulePersistenceEmbeddable;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.entities.RouteAssignmentPersistenceEntity;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.entities.RoutePersistenceEntity;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.entities.RouteStopPersistenceEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Static assembler between route domain and persistence representations.
 */
public final class RoutePersistenceAssembler {

    private RoutePersistenceAssembler() {
    }

    public static Route toDomainFromPersistence(RoutePersistenceEntity entity) {
        if (entity == null) return null;
        List<RouteStop> stops = entity.getStops().stream()
                .map(stop -> new RouteStop(stop.getId(), stop.getAddress(), stop.getSchoolId(), stop.getPosition()))
                .toList();
        List<RouteSchedule> schedule = entity.getSchedule().stream()
                .map(row -> new RouteSchedule(row.getDayOfWeek(), row.getDepartureTime()))
                .toList();
        List<RouteAssignment> assignments = entity.getAssignments().stream()
                .map(assignment -> new RouteAssignment(
                        assignment.getId(), assignment.getStop().getId(), assignment.getStudentId()))
                .toList();
        return Route.reconstitute(entity.getId(), entity.getDriverProfileId(), entity.getName(), entity.getCode(), entity.getShift(),
                entity.getStatus(), entity.getVehicleId(), entity.getDepartureTime(), entity.isScheduleConfirmed(),
                stops, schedule, assignments);
    }

    public static RoutePersistenceEntity toPersistenceFromDomain(Route route) {
        if (route == null) return null;
        var entity = new RoutePersistenceEntity();
        // Only set ID if the route is being updated; for new routes JPA generates it
        if (route.getId() != null) {
            entity.setId(route.getId());
        }
        entity.setDriverProfileId(route.getDriverProfileId());
        entity.setName(route.getName());
        entity.setCode(route.getCode());
        entity.setShift(route.getShift());
        entity.setStatus(route.getStatus());
        entity.setVehicleId(route.getVehicleId());
        entity.setDepartureTime(route.getDepartureTime());
        entity.setScheduleConfirmed(route.isScheduleConfirmed());

        var stopsById = new HashMap<Long, RouteStopPersistenceEntity>();
        var stops = new ArrayList<RouteStopPersistenceEntity>();
        for (var stop : route.getStops()) {
            var persistenceStop = new RouteStopPersistenceEntity();
            if (stop.getId() != null) {
                persistenceStop.setId(stop.getId());
                stopsById.put(stop.getId(), persistenceStop);
            }
            persistenceStop.setRoute(entity);
            persistenceStop.setAddress(stop.getAddress());
            persistenceStop.setSchoolId(stop.getSchoolId());
            persistenceStop.setPosition(stop.getPosition());
            stops.add(persistenceStop);
        }
        entity.setStops(stops);

        var assignments = new ArrayList<RouteAssignmentPersistenceEntity>();
        for (var assignment : route.getAssignments()) {
            var persistenceStop = stopsById.get(assignment.getStopId());
            if (persistenceStop == null) {
                throw new IllegalArgumentException("Assigned stop is not part of this route");
            }
            var persistenceAssignment = new RouteAssignmentPersistenceEntity();
            if (assignment.getId() != null) {
                persistenceAssignment.setId(assignment.getId());
            }
            persistenceAssignment.setRoute(entity);
            persistenceAssignment.setStop(persistenceStop);
            persistenceAssignment.setStudentId(assignment.getStudentId());
            assignments.add(persistenceAssignment);
        }
        entity.setAssignments(assignments);

        entity.setSchedule(new ArrayList<>(route.getSchedule().stream()
                .map(row -> new RouteSchedulePersistenceEmbeddable(row.dayOfWeek(), row.departureTime()))
                .toList()));
        return entity;
    }
}
