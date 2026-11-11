package com.aipaca.rumbo.routes.interfaces.rest;
import com.aipaca.rumbo.routes.domain.model.RouteShift;
import com.aipaca.rumbo.routes.interfaces.rest.resources.RouteResources.*;
import com.aipaca.rumbo.routes.interfaces.rest.transform.RouteCommandFromResourceAssembler;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class RouteCommandFromResourceAssemblerTests {
    @Test void createsOrderedStopsAndSchedule() {
        var r=new RouteInput("Ruta Surco",RouteShift.MORNING,"vehicle-1",null,
            List.of(new StopInput(null,"A",null),new StopInput(null,"B",null)),
            List.of(new ScheduleInput(java.time.DayOfWeek.MONDAY,"07:00")),true);
        var c=RouteCommandFromResourceAssembler.toCommand(r);
        assertEquals(2,c.stops().size());
        assertEquals(2,c.stops().get(1).getPosition());
        assertTrue(c.scheduleConfirmed());
    }
    @Test void rejectsInvalidTime() {
        var r=new RouteInput("Ruta Surco",RouteShift.MORNING,"vehicle-1",null,List.of(),
            List.of(new ScheduleInput(java.time.DayOfWeek.MONDAY,"25:80")),true);
        assertThrows(IllegalArgumentException.class,()->RouteCommandFromResourceAssembler.toCommand(r));
    }
}
