package com.aipaca.rumbo.platform.trips.domain.model.aggregates;

import com.aipaca.rumbo.platform.trips.domain.model.commands.PlanTripCommand;
import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteShift;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.TripStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripTests {
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 16);
    private static final StudentId FIRST = new StudentId(5L);
    private static final StudentId SECOND = new StudentId(6L);

    private static Trip plannedTrip() {
        return new Trip(new PlanTripCommand(1L, 2L, 3L, MONDAY, RouteShift.MORNING, List.of(
                new TripPassenger(SECOND, 11L, 2, "Parada 2"),
                new TripPassenger(FIRST, 10L, 1, "Parada 1"))));
    }

    @Test
    void plannedTripKeepsTheRouteIdentityAndOrdersPassengersByStop() {
        var trip = plannedTrip();
        assertEquals(TripStatus.SCHEDULED, trip.getStatus());
        assertEquals(1L, trip.getRouteIdValue());
        assertEquals(2L, trip.getDriverProfileIdValue());
        assertEquals(3L, trip.getVehicleIdValue());
        assertEquals("Parada 1", trip.getPassengers().getFirst().getStopAddress());
        assertEquals(List.of(FIRST, SECOND), trip.getPassengers().stream().map(TripPassenger::getStudentId).toList());
    }

    @Test
    void planRequiresStudentsWithoutDuplicates() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(new PlanTripCommand(1L, 2L, 3L, MONDAY, RouteShift.MORNING, List.of())));
        assertThrows(IllegalArgumentException.class, () -> new Trip(new PlanTripCommand(1L, 2L, 3L, MONDAY, RouteShift.MORNING,
                List.of(new TripPassenger(FIRST, 10L, 1, "A"), new TripPassenger(FIRST, 11L, 2, "B")))));
        assertThrows(IllegalArgumentException.class, () -> new PlanTripCommand(1L, 2L, 3L, null, RouteShift.MORNING, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new PlanTripCommand(1L, null, 3L, MONDAY, RouteShift.MORNING, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new PlanTripCommand(1L, 2L, 0L, MONDAY, RouteShift.MORNING, List.of()));
    }

    @Test
    void arrivalRequiresServedStopAndActiveTrip() {
        var trip = plannedTrip();
        assertThrows(IllegalArgumentException.class, () -> trip.confirmStudentArrival(FIRST));
        trip.start();
        assertThrows(IllegalArgumentException.class, () -> trip.confirmStudentArrival(FIRST));
        trip.serveStop(10L);
        trip.confirmStudentArrival(FIRST);
        assertTrue(trip.getPassengers().getFirst().isArrived());
        assertThrows(IllegalArgumentException.class, () -> trip.confirmStudentArrival(FIRST));
    }

    @Test
    void absentStudentCannotArriveAndServedStopRejectsAbsence() {
        var trip = plannedTrip();
        trip.markStudentAbsent(SECOND);
        trip.start();
        trip.serveStop(10L);
        assertThrows(IllegalArgumentException.class, () -> trip.markStudentAbsent(FIRST));
        trip.serveStop(11L);
        assertThrows(IllegalArgumentException.class, () -> trip.confirmStudentArrival(SECOND));
    }

    @Test
    void lifecycleTransitionsAreGuarded() {
        var trip = plannedTrip();
        assertThrows(IllegalArgumentException.class, trip::complete);
        trip.start();
        assertThrows(IllegalArgumentException.class, trip::start);
        trip.complete();
        assertEquals(TripStatus.COMPLETED, trip.getStatus());
        assertThrows(IllegalArgumentException.class, () -> trip.cancel("Late"));
    }
}
