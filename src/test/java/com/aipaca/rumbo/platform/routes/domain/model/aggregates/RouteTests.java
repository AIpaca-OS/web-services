package com.aipaca.rumbo.platform.routes.domain.model.aggregates;

import com.aipaca.rumbo.platform.routes.domain.model.commands.CreateRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTests {

    private static final List<RouteSchedule> MONDAY = List.of(new RouteSchedule(DayOfWeek.MONDAY, LocalTime.of(7, 0)));

    private static Route persistedRoute(Long vehicleId, boolean confirmed, List<RouteAssignment> assignments) {
        return Route.reconstitute(1L, new DriverProfileId(2L), "Ruta Surco", "R-ABCDEF12", RouteShift.MORNING, RouteStatus.CONFIGURABLE,
                vehicleId == null ? null : new VehicleId(vehicleId), LocalTime.of(7, 0), confirmed,
                List.of(new RouteStop(10L, "Av. Primavera 123", 1), new RouteStop(11L, "Jr. Flores 456", 2)),
                MONDAY, assignments);
    }

    @Test
    void createRouteIsConfigurableWithOrderedStopsAndCode() {
        var route = new Route(new CreateRouteCommand(2L, "  Ruta Surco ", RouteShift.MORNING, 1L,
                List.of(new RouteStop(null, "Av. Primavera 123", 1), new RouteStop(null, "Jr. Flores 456", 2)),
                MONDAY, true));
        assertEquals("Ruta Surco", route.getName());
        assertEquals(RouteStatus.CONFIGURABLE, route.getStatus());
        assertTrue(route.getCode().matches("R-[0-9A-F]{8}"));
        assertEquals(List.of(1, 2), route.getStops().stream().map(RouteStop::getPosition).toList());
        assertEquals(LocalTime.of(7, 0), route.getDepartureTime());
        assertEquals(1L, route.getVehicleIdValue());
        assertEquals(2L, route.getDriverProfileIdValue());
        assertEquals("Av. Primavera 123", route.getStops().getFirst().getAddress());
        assertEquals("Jr. Flores 456", route.getStops().get(1).getAddress());
        assertThrows(IllegalArgumentException.class, () -> new CreateRouteCommand(null, "Ruta", RouteShift.MORNING,
                null, List.of(), List.of(), false));
    }

    @Test
    void scheduleRejectsDuplicateDaysAndEmptyConfirmedSchedule() {
        var route = persistedRoute(1L, false, List.of());
        var duplicated = List.of(new RouteSchedule(DayOfWeek.MONDAY, LocalTime.of(7, 0)),
                new RouteSchedule(DayOfWeek.MONDAY, LocalTime.of(8, 0)));
        assertThrows(IllegalArgumentException.class, () -> route.updateSchedule(duplicated, false));
        assertThrows(IllegalArgumentException.class, () -> route.updateSchedule(List.of(), true));
    }

    @Test
    void assignedStopCannotBeRemovedAndUnknownStopIdsAreRejected() {
        var route = persistedRoute(1L, true, List.of(new RouteAssignment(1L, 10L, new StudentId(5L))));
        assertThrows(IllegalArgumentException.class,
                () -> route.replaceStops(List.of(new RouteStop(11L, "Jr. Flores 456", 1))));
        assertThrows(IllegalArgumentException.class,
                () -> route.replaceStops(List.of(new RouteStop(99L, "Otra", 1))));
        route.replaceStops(List.of(new RouteStop(null, "Nueva", 1), new RouteStop(10L, "Av. Primavera 123", 2)));
        assertEquals(List.of(1, 2), route.getStops().stream().map(RouteStop::getPosition).toList());
    }

    @Test
    void studentIsAssignedOnlyOnceAndOnlyToStopsOfTheRoute() {
        var route = persistedRoute(1L, true, List.of());
        route.assignStudent(10L, new StudentId(5L));
        assertTrue(route.isStudentAssigned(new StudentId(5L)));
        assertThrows(IllegalArgumentException.class, () -> route.assignStudent(11L, new StudentId(5L)));
        assertThrows(IllegalArgumentException.class, () -> route.assignStudent(99L, new StudentId(6L)));
    }

    @Test
    void replacingAssignmentsKeepsTheIdentityOfStudentsAlreadyAssigned() {
        var route = persistedRoute(1L, true, List.of(new RouteAssignment(20L, 10L, new StudentId(5L))));
        route.replaceAssignments(List.of(new RouteAssignment(11L, new StudentId(5L)),
                new RouteAssignment(10L, new StudentId(6L))));
        var kept = route.findAssignment(new StudentId(5L)).orElseThrow();
        assertEquals(20L, kept.getId());
        assertEquals(11L, kept.getStopId());
        assertNull(route.findAssignment(new StudentId(6L)).orElseThrow().getId());
        assertThrows(IllegalArgumentException.class, () -> route.replaceAssignments(List.of(
                new RouteAssignment(10L, new StudentId(7L)), new RouteAssignment(11L, new StudentId(7L)))));
    }

    @Test
    void publicationRequiresVehicleConfirmedScheduleAndStudents() {
        assertThrows(IllegalArgumentException.class, () -> persistedRoute(1L, true, List.of()).publish());
        var withoutVehicle = persistedRoute(null, true, List.of(new RouteAssignment(1L, 10L, new StudentId(5L))));
        assertThrows(IllegalArgumentException.class, withoutVehicle::publish);
        var route = persistedRoute(1L, true, List.of(new RouteAssignment(1L, 10L, new StudentId(5L))));
        route.publish();
        assertTrue(route.isPublished());
        assertThrows(IllegalArgumentException.class, route::publish);
    }

    @Test
    void publishedRouteCannotChangeVehicleNorAssignments() {
        var route = persistedRoute(1L, true, List.of(new RouteAssignment(1L, 10L, new StudentId(5L))));
        route.publish();
        assertThrows(IllegalArgumentException.class, () -> route.updateInformation("Ruta", RouteShift.MORNING, 2L));
        assertThrows(IllegalArgumentException.class, () -> route.assignStudent(11L, new StudentId(6L)));
        assertDoesNotThrow(() -> route.updateInformation("Ruta nueva", RouteShift.AFTERNOON, 1L));
    }

    @Test
    void identityValueObjectsRejectInvalidIds() {
        assertThrows(IllegalArgumentException.class, () -> new StudentId(0L));
        assertThrows(IllegalArgumentException.class, () -> new VehicleId(null));
    }
}
