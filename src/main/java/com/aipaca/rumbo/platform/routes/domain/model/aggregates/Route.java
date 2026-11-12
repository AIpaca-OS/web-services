package com.aipaca.rumbo.platform.routes.domain.model.aggregates;

import com.aipaca.rumbo.platform.routes.domain.model.commands.CreateRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

/**
 * Route aggregate root.
 *
 * <p>Owns its ordered stops, its weekly schedule and the assignment of students to stops.
 * Drivers, students, schools and vehicles belong to other bounded contexts, so the route only keeps
 * their identities through {@link DriverProfileId}, {@link StudentId}, {@link SchoolId} and {@link VehicleId}. No JPA or persistence
 * annotation is present here — those concerns live in {@code RoutePersistenceEntity}.</p>
 */
@Getter
public class Route extends AbstractDomainAggregateRoot<Route> {
    private Long id;
    private DriverProfileId driverProfileId;
    private String name;
    private String code;
    private RouteShift shift;
    private RouteStatus status;
    private VehicleId vehicleId;
    private LocalTime departureTime;
    private boolean scheduleConfirmed;
    private final List<RouteStop> stops = new ArrayList<>();
    private final List<RouteSchedule> schedule = new ArrayList<>();
    private final List<RouteAssignment> assignments = new ArrayList<>();

    private Route() {
    }

    /**
     * Creates a configurable route from a {@link CreateRouteCommand}.
     *
     * @param command The {@link CreateRouteCommand} instance
     */
    public Route(CreateRouteCommand command) {
        this.code = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        this.status = RouteStatus.CONFIGURABLE;
        this.driverProfileId = new DriverProfileId(command.driverProfileId());
        rename(command.name());
        this.shift = command.shift();
        this.vehicleId = command.vehicleId() == null ? null : new VehicleId(command.vehicleId());
        replaceStops(command.stops());
        updateSchedule(command.schedule(), command.scheduleConfirmed());
    }

    /**
     * Reconstitutes an existing route from persistence state.
     *
     * @return The reconstituted Route instance.
     */
    public static Route reconstitute(Long id, DriverProfileId driverProfileId, String name, String code, RouteShift shift, RouteStatus status,
                                     VehicleId vehicleId, LocalTime departureTime, boolean scheduleConfirmed,
                                     List<RouteStop> stops, List<RouteSchedule> schedule,
                                     List<RouteAssignment> assignments) {
        var route = new Route();
        route.id = id;
        route.driverProfileId = driverProfileId;
        route.name = name;
        route.code = code;
        route.shift = shift;
        route.status = status;
        route.vehicleId = vehicleId;
        route.departureTime = departureTime;
        route.scheduleConfirmed = scheduleConfirmed;
        if (stops != null) route.stops.addAll(stops);
        if (schedule != null) route.schedule.addAll(schedule);
        if (assignments != null) route.assignments.addAll(assignments);
        return route;
    }

    /**
     * Updates the general information of the route. A published route cannot change its vehicle.
     */
    public Route updateInformation(String name, RouteShift shift, Long vehicleId) {
        var newVehicleId = vehicleId == null ? null : new VehicleId(vehicleId);
        if (isPublished() && !Objects.equals(this.vehicleId, newVehicleId)) {
            throw new IllegalArgumentException("A published route cannot change vehicle");
        }
        rename(name);
        this.shift = Objects.requireNonNull(shift, "Route shift cannot be null");
        this.vehicleId = newVehicleId;
        return this;
    }

    /**
     * Replaces the ordered stops. Positions are recalculated from the list order.
     * A stop that still has assigned students cannot be removed.
     */
    public void replaceStops(List<RouteStop> proposed) {
        if (proposed == null) throw new IllegalArgumentException("Stops cannot be null");
        var currentIds = new HashSet<Long>();
        stops.forEach(stop -> currentIds.add(stop.getId()));
        var proposedIds = new HashSet<Long>();
        for (var stop : proposed) {
            if (stop.getId() == null) continue;
            if (!currentIds.contains(stop.getId()))
                throw new IllegalArgumentException("Stop %d is not part of this route".formatted(stop.getId()));
            if (!proposedIds.add(stop.getId()))
                throw new IllegalArgumentException("Duplicate stop id %d".formatted(stop.getId()));
        }
        if (assignments.stream().anyMatch(assignment -> !proposedIds.contains(assignment.getStopId())))
            throw new IllegalArgumentException("Cannot remove a stop with assigned students");
        stops.clear();
        for (int i = 0; i < proposed.size(); i++) {
            var stop = proposed.get(i);
            stops.add(new RouteStop(stop.getId(), stop.getAddress(), stop.getSchoolId(), i + 1));
        }
    }

    /**
     * Replaces the weekly schedule. Each service day can appear only once.
     */
    public void updateSchedule(List<RouteSchedule> proposed, boolean confirmed) {
        if (proposed == null) throw new IllegalArgumentException("Schedule cannot be null");
        var days = new HashSet<DayOfWeek>();
        for (var row : proposed) {
            if (!days.add(row.dayOfWeek())) throw new IllegalArgumentException("Duplicate schedule day");
        }
        if (confirmed && proposed.isEmpty()) throw new IllegalArgumentException("Confirmed schedule cannot be empty");
        schedule.clear();
        schedule.addAll(proposed);
        scheduleConfirmed = confirmed;
        departureTime = schedule.isEmpty() ? null : schedule.getFirst().departureTime();
    }

    /**
     * Assigns a student to a stop of a configurable route.
     */
    public void assignStudent(Long stopId, StudentId studentId) {
        requireConfigurable();
        if (!hasStop(stopId)) throw new IllegalArgumentException("Stop is not part of this route");
        if (isStudentAssigned(studentId)) throw new IllegalArgumentException("Student is already assigned to this route");
        assignments.add(new RouteAssignment(stopId, studentId));
    }

    /**
     * Replaces every student-to-stop assignment of a configurable route.
     * A student that was already assigned keeps the identity of its assignment.
     */
    public void replaceAssignments(List<RouteAssignment> proposed) {
        requireConfigurable();
        if (proposed == null) throw new IllegalArgumentException("Assignments cannot be null");
        var students = new HashSet<StudentId>();
        var replacement = new ArrayList<RouteAssignment>();
        for (var assignment : proposed) {
            if (!hasStop(assignment.getStopId()))
                throw new IllegalArgumentException("Assigned stop is not part of this route");
            if (!students.add(assignment.getStudentId()))
                throw new IllegalArgumentException("Student is assigned more than once");
            var currentId = findAssignment(assignment.getStudentId()).map(RouteAssignment::getId).orElse(null);
            replacement.add(new RouteAssignment(currentId, assignment.getStopId(), assignment.getStudentId()));
        }
        assignments.clear();
        assignments.addAll(replacement);
    }

    /**
     * Publishes the route. It requires stops, a confirmed schedule, a vehicle and at least one student.
     */
    public void publish() {
        if (isPublished()) throw new IllegalArgumentException("Route has already been published");
        if (stops.isEmpty() || !scheduleConfirmed || schedule.isEmpty() || assignments.isEmpty())
            throw new IllegalArgumentException("Publishing requires stops, confirmed schedule and an assigned student");
        if (vehicleId == null)
            throw new IllegalArgumentException("Publishing requires a vehicle");
        status = RouteStatus.PUBLISHED;
    }

    public boolean isPublished() {
        return status == RouteStatus.PUBLISHED;
    }

    public boolean hasStop(Long stopId) {
        return stops.stream().anyMatch(stop -> Objects.equals(stop.getId(), stopId));
    }

    public boolean isStudentAssigned(StudentId studentId) {
        return assignments.stream().anyMatch(assignment -> assignment.getStudentId().equals(studentId));
    }

    public boolean operatesOn(DayOfWeek day) {
        return scheduleConfirmed && schedule.stream().anyMatch(row -> row.dayOfWeek() == day);
    }

    public Optional<RouteAssignment> findAssignment(StudentId studentId) {
        return assignments.stream().filter(assignment -> assignment.getStudentId().equals(studentId)).findFirst();
    }

    public Long getVehicleIdValue() {
        return vehicleId == null ? null : vehicleId.vehicleId();
    }

    public Long getDriverProfileIdValue() {
        return driverProfileId.driverProfileId();
    }

    public List<RouteStop> getStops() {
        return List.copyOf(stops);
    }

    public List<RouteSchedule> getSchedule() {
        return List.copyOf(schedule);
    }

    public List<RouteAssignment> getAssignments() {
        return List.copyOf(assignments);
    }

    private void rename(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Route name cannot be null or blank");
        this.name = name.trim();
    }

    private void requireConfigurable() {
        if (isPublished()) throw new IllegalArgumentException("Published routes cannot be reconfigured");
    }
}
