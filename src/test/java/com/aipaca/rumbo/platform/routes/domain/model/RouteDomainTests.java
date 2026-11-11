package com.aipaca.rumbo.platform.routes.domain.model;

import com.aipaca.rumbo.platform.routes.domain.model.*;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class RouteDomainTests {
    private Route validRoute() {
        var route=new Route("Morning school route", RouteShift.MORNING,"vehicle-1");
        var stop=new RouteStop(UUID.randomUUID(),"Av. Primavera 123",1);
        route.replaceStops(List.of(stop));
        route.updateSchedule(List.of(new RouteSchedule(DayOfWeek.MONDAY,LocalTime.of(7,0))),true);
        route.assign(stop.getId(),"student-1");
        return route;
    }
    @Test void routeStartsConfigurable() {
        var r=new Route("Test",RouteShift.MORNING,null);
        assertEquals(RouteStatus.CONFIGURABLE,r.getStatus());
        assertThrows(IllegalArgumentException.class,r::publish);
    }
    @Test void routePublishesOnlyWithRequiredData() {
        var r=validRoute();r.publish();
        assertEquals(RouteStatus.PUBLISHED,r.getStatus());
    }
    @Test void duplicateStudentsAreRejected() {
        var r=validRoute();
        assertThrows(IllegalArgumentException.class,()->r.assign(r.getStops().getFirst().getId(),"student-1"));
    }
    @Test void duplicateDaysAreRejected() {
        var r=new Route("Test",RouteShift.MORNING,null);
        var m=new RouteSchedule(DayOfWeek.MONDAY,LocalTime.of(7,0));
        assertThrows(IllegalArgumentException.class,()->r.updateSchedule(List.of(m,m),true));
    }
    @Test void tripKeepsSnapshotWhenRouteStopsChange() {
        var r=validRoute();r.publish();
        var t=new Trip(r,LocalDate.of(2026,11,2)); // Monday
        var original=t.getPassengers().getFirst().getStopAddress();
        r.replaceStops(List.of(new RouteStop(r.getStops().getFirst().getId(),"Changed address",1)));
        assertEquals(original,t.getPassengers().getFirst().getStopAddress());
    }
    @Test void absentStudentIsExcludedFromPickup() {
        var r=validRoute();r.publish();
        var t=new Trip(r,LocalDate.of(2026,11,2));
        t.markAbsent("student-1");
        assertTrue(t.getPassengers().getFirst().isAbsent());
    }
    @Test void cancellationRequiresReasonAndPreservesStatus() {
        var r=validRoute();r.publish();
        var t=new Trip(r,LocalDate.of(2026,11,2));
        assertThrows(IllegalArgumentException.class,()->t.cancel("  "));
        t.cancel("Mechanical problem");
        assertEquals(TripStatus.CANCELLED,t.getStatus());
        assertThrows(IllegalArgumentException.class,()->t.cancel("again"));
    }

    @Test void routeCannotPublishWithoutVehicle() {
        var route = validRoute();
        route.setVehicleId(null);
        assertThrows(IllegalArgumentException.class, route::publish);
    }

    @Test void cannotRemoveStopWithAssignedStudent() {
        var route = validRoute();
        assertThrows(IllegalArgumentException.class, () -> route.replaceStops(List.of()));
    }

    @Test void stopsAreStoredInUserDefinedOrder() {
        var route = new Route("School route", RouteShift.MORNING, "vehicle-1");
        var first = new RouteStop(UUID.randomUUID(), "First address", 9);
        var second = new RouteStop(UUID.randomUUID(), "Second address", 2);
        route.replaceStops(List.of(first, second));
        assertEquals(1, route.getStops().get(0).getPosition());
        assertEquals("Second address", route.getStops().get(1).getAddress());
        assertEquals(2, route.getStops().get(1).getPosition());
    }
}
