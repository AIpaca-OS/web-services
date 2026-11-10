package com.aipaca.rumbo.routes.application.internal;

import com.aipaca.rumbo.routes.application.internal.commandservices.RouteCommandServiceImpl;
import com.aipaca.rumbo.routes.application.ports.StudentAuthorizationPort;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.model.commands.*;
import com.aipaca.rumbo.routes.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Tests Routes rules using an explicit Profiles authorization contract. */
class RouteAssignmentAuthorizationTests {
    private RouteRepository routes;
    private StudentAuthorizationPort profiles;
    private RouteCommandServiceImpl commands;
    private Route route;
    private UUID stopId;
    private static final String TOKEN = "Bearer driver-jwt-example";

    @BeforeEach
    void setUp() {
        routes = mock(RouteRepository.class);
        profiles = mock(StudentAuthorizationPort.class);
        commands = new RouteCommandServiceImpl(routes, mock(TripRepository.class), profiles);
        route = new Route("Morning trip", RouteShift.MORNING, "vehicle-1");
        stopId = UUID.randomUUID();
        route.replaceStops(List.of(new RouteStop(stopId, "Av. Primavera 123", 1)));
        route.updateSchedule(List.of(
                new RouteSchedule(DayOfWeek.MONDAY, LocalTime.of(7, 0))), true);
        when(routes.findById(route.getId())).thenReturn(Optional.of(route));
        when(routes.save(any(Route.class))).thenAnswer(call -> call.getArgument(0));
    }

    @Test void authorizedStudentCanBeAssigned() {
        when(profiles.isAuthorized("student-1", "vehicle-1", TOKEN)).thenReturn(true);
        var assignment = commands.handle(new AssignStudentCommand(route.getId(), stopId, "student-1", TOKEN));
        assertEquals("student-1", assignment.getStudentId());
        assertEquals(1, route.getAssignments().size());
        verify(routes).save(route);
    }

    @Test void unauthorizedStudentCannotBeAssigned() {
        when(profiles.isAuthorized("student-1", "vehicle-1", TOKEN)).thenReturn(false);
        var exception = assertThrows(ResponseStatusException.class,
                () -> commands.handle(new AssignStudentCommand(route.getId(), stopId, "student-1", TOKEN)));
        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
        assertTrue(route.getAssignments().isEmpty());
        verify(routes, never()).save(any());
    }

    @Test void duplicateAssignmentReturnsConflict() {
        when(profiles.isAuthorized("student-1", "vehicle-1", TOKEN)).thenReturn(true);
        commands.handle(new AssignStudentCommand(route.getId(), stopId, "student-1", TOKEN));
        var exception = assertThrows(ResponseStatusException.class,
                () -> commands.handle(new AssignStudentCommand(route.getId(), stopId, "student-1", TOKEN)));
        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        assertEquals(1, route.getAssignments().size());
    }

    @Test void publicationRechecksCurrentProfilesAuthorization() {
        when(profiles.isAuthorized("student-1", "vehicle-1", TOKEN)).thenReturn(true, false);
        commands.handle(new AssignStudentCommand(route.getId(), stopId, "student-1", TOKEN));

        var exception = assertThrows(ResponseStatusException.class,
                () -> commands.handle(new PublishRouteCommand(route.getId(), TOKEN)));
        assertEquals(HttpStatus.FORBIDDEN.value(), exception.getStatusCode().value());
        assertEquals(RouteStatus.CONFIGURABLE, route.getStatus());
        verify(routes, times(1)).save(route);
    }

    @Test void routePublishesOnlyWhenAuthorizationIsStillValid() {
        when(profiles.isAuthorized("student-1", "vehicle-1", TOKEN)).thenReturn(true);
        commands.handle(new AssignStudentCommand(route.getId(), stopId, "student-1", TOKEN));
        var published = commands.handle(new PublishRouteCommand(route.getId(), TOKEN));
        assertEquals(RouteStatus.PUBLISHED, published.getStatus());
    }
}
