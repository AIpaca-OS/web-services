package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.commands.CreateRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoutePersistenceAssemblerTests {

    @Test
    void newRouteKeepsIdsNullSoJpaGeneratesThem() {
        var route = new Route(new CreateRouteCommand(2L, "Ruta", RouteShift.MORNING, null,
                List.of(new RouteStop(null, "Parada 1", new SchoolId(4L), 1)), List.of(), false));
        var entity = RoutePersistenceAssembler.toPersistenceFromDomain(route);
        assertNull(entity.getId());
        assertNull(entity.getStops().getFirst().getId());
        assertSame(entity, entity.getStops().getFirst().getRoute());
    }

    @Test
    void assignmentReferencesTheSameStopEntityOfTheGraphAndRoundTrips() {
        var route = Route.reconstitute(1L, new DriverProfileId(2L), "Ruta", "R-1", RouteShift.MORNING, RouteStatus.CONFIGURABLE,
                new VehicleId(3L), LocalTime.of(7, 0), true,
                List.of(new RouteStop(10L, "Parada 1", new SchoolId(4L), 1), new RouteStop(11L, "Parada 2", 2)),
                List.of(new RouteSchedule(DayOfWeek.MONDAY, LocalTime.of(7, 0))),
                List.of(new RouteAssignment(20L, 11L, new StudentId(5L))));

        var entity = RoutePersistenceAssembler.toPersistenceFromDomain(route);
        var assignment = entity.getAssignments().getFirst();
        assertSame(entity.getStops().get(1), assignment.getStop());
        assertSame(entity, assignment.getRoute());

        var restored = RoutePersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(1L, restored.getId());
        assertEquals(3L, restored.getVehicleIdValue());
        assertEquals(2L, restored.getDriverProfileIdValue());
        assertEquals(4L, restored.getStops().getFirst().getSchoolIdValue());
        assertEquals(11L, restored.getAssignments().getFirst().getStopId());
        assertEquals(new StudentId(5L), restored.getAssignments().getFirst().getStudentId());
        assertEquals(DayOfWeek.MONDAY, restored.getSchedule().getFirst().dayOfWeek());
    }
}
