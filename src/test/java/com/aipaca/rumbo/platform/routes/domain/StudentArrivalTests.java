package com.aipaca.rumbo.platform.routes.domain;

import com.aipaca.rumbo.platform.routes.domain.model.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class StudentArrivalTests {
    @Test void arrivalIsExplicitAndRequiresServedStop() {
        UUID stopId = UUID.randomUUID();
        Route route = new Route("Ruta colegio", RouteShift.MORNING, "1");
        route.replaceStops(List.of(new RouteStop(stopId,"Av. Escolar 123",1)));
        route.updateSchedule(List.of(new RouteSchedule(LocalDate.now().getDayOfWeek(),
                LocalTime.of(7,30))), true);
        route.assign(stopId,"123");
        route.publish();
        Trip trip=new Trip(route,LocalDate.now());
        trip.start();
        assertThrows(IllegalArgumentException.class,()->trip.confirmStudentArrival("123"));
        trip.serveStop(stopId);
        assertFalse(trip.getPassengers().getFirst().isArrived());
        trip.confirmStudentArrival("123");
        assertTrue(trip.getPassengers().getFirst().isArrived());
        assertThrows(IllegalArgumentException.class,()->trip.confirmStudentArrival("123"));
    }
}
