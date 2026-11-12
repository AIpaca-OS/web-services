package com.aipaca.rumbo.platform.trips.domain.model.aggregates;

import com.aipaca.rumbo.platform.trips.domain.model.commands.PlanTripCommand;
import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteShift;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.TripStatus;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.VehicleId;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

/**
 * Trip aggregate root.
 *
 * <p>A trip is planned from a published route of the Routes bounded context for one service date.
 * It keeps a snapshot of the route assignments as {@link TripPassenger} entities and references
 * the route, its driver and its vehicle only by identity through {@link RouteId},
 * {@link DriverProfileId} and {@link VehicleId}.</p>
 */
@Getter
public class Trip extends AbstractDomainAggregateRoot<Trip> {
    private Long id;
    private RouteId routeId;
    private DriverProfileId driverProfileId;
    private VehicleId vehicleId;
    private LocalDate serviceDate;
    private TripStatus status;
    private RouteShift shift;
    private String cancelReason;
    private final List<TripPassenger> passengers = new ArrayList<>();

    private Trip() {
    }

    /**
     * Plans a trip from the snapshot of a published route.
     *
     * @param command The {@link PlanTripCommand} instance
     */
    public Trip(PlanTripCommand command) {
        if (command.passengers().isEmpty())
            throw new IllegalArgumentException("A trip requires at least one assigned student");
        var students = new HashSet<StudentId>();
        for (var passenger : command.passengers()) {
            if (!students.add(passenger.getStudentId()))
                throw new IllegalArgumentException("Student is assigned more than once");
        }
        this.routeId = new RouteId(command.routeId());
        this.driverProfileId = new DriverProfileId(command.driverProfileId());
        this.vehicleId = new VehicleId(command.vehicleId());
        this.serviceDate = command.serviceDate();
        this.shift = command.shift();
        this.status = TripStatus.SCHEDULED;
        this.passengers.addAll(command.passengers());
        this.passengers.sort(Comparator.comparingInt(TripPassenger::getStopPosition));
    }

    /**
     * Reconstitutes an existing trip from persistence state.
     *
     * @return The reconstituted Trip instance.
     */
    public static Trip reconstitute(Long id, RouteId routeId, DriverProfileId driverProfileId,
                                    VehicleId vehicleId, LocalDate serviceDate, TripStatus status,
                                    RouteShift shift, String cancelReason, List<TripPassenger> passengers) {
        var trip = new Trip();
        trip.id = id;
        trip.routeId = routeId;
        trip.driverProfileId = driverProfileId;
        trip.vehicleId = vehicleId;
        trip.serviceDate = serviceDate;
        trip.status = status;
        trip.shift = shift;
        trip.cancelReason = cancelReason;
        if (passengers != null) trip.passengers.addAll(passengers);
        trip.passengers.sort(Comparator.comparingInt(TripPassenger::getStopPosition));
        return trip;
    }

    public void markStudentAbsent(StudentId studentId) {
        if (status != TripStatus.SCHEDULED && status != TripStatus.IN_PROGRESS)
            throw new IllegalArgumentException("Trip is no longer accepting absences");
        requiredPassenger(studentId).markAbsent();
    }

    public void start() {
        if (status != TripStatus.SCHEDULED) throw new IllegalArgumentException("Trip cannot be started");
        status = TripStatus.IN_PROGRESS;
    }

    public void serveStop(Long stopId) {
        if (status != TripStatus.IN_PROGRESS) throw new IllegalArgumentException("Trip is not in progress");
        var atStop = passengers.stream().filter(passenger -> passenger.getStopId().equals(stopId)).toList();
        if (atStop.isEmpty()) throw new IllegalArgumentException("Stop is not on trip");
        atStop.forEach(TripPassenger::markStopServed);
    }

    /**
     * Records the driver's explicit confirmation that a student arrived.
     */
    public void confirmStudentArrival(StudentId studentId) {
        if (status != TripStatus.IN_PROGRESS)
            throw new IllegalArgumentException("Arrival can only be confirmed during an active trip");
        requiredPassenger(studentId).confirmArrival();
    }

    public void cancel(String reason) {
        if (status == TripStatus.CANCELLED || status == TripStatus.COMPLETED)
            throw new IllegalArgumentException("Trip cannot be cancelled");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Cancellation reason is required");
        cancelReason = reason.trim();
        status = TripStatus.CANCELLED;
    }

    public void complete() {
        if (status != TripStatus.IN_PROGRESS) throw new IllegalArgumentException("Trip is not in progress");
        status = TripStatus.COMPLETED;
    }

    public Long getRouteIdValue() {
        return routeId.routeId();
    }

    public Long getDriverProfileIdValue() {
        return driverProfileId.driverProfileId();
    }

    public Long getVehicleIdValue() {
        return vehicleId.vehicleId();
    }

    public List<TripPassenger> getPassengers() {
        return List.copyOf(passengers);
    }

    private TripPassenger requiredPassenger(StudentId studentId) {
        return passengers.stream().filter(passenger -> passenger.getStudentId().equals(studentId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Student is not on trip"));
    }
}
