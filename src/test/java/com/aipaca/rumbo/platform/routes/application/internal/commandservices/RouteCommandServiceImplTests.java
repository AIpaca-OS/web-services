package com.aipaca.rumbo.platform.routes.application.internal.commandservices;
import org.springframework.context.ApplicationEventPublisher;

import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.commands.AssignStudentToRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.commands.DeleteRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.commands.PublishRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RouteCommandServiceImplTests {
    private RouteRepository routes;
    private ApplicationEventPublisher events;
    private RouteCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        routes = mock(RouteRepository.class);
        events = mock(ApplicationEventPublisher.class);
        service = new RouteCommandServiceImpl(routes, events);
    }

    private static Route route(List<RouteAssignment> assignments) {
        return Route.reconstitute(1L, new DriverProfileId(2L), "Ruta", "R-1", RouteShift.MORNING, RouteStatus.CONFIGURABLE,
                new VehicleId(1L), LocalTime.of(7, 0), true, List.of(new RouteStop(10L, "Parada", 1)),
                List.of(new RouteSchedule(DayOfWeek.MONDAY, LocalTime.of(7, 0))), assignments);
    }

    @Test
    void missingRouteIsNotFoundAndNothingIsSaved() {
        when(routes.findById(1L)).thenReturn(Optional.empty());
        assertEquals("ROUTE_NOT_FOUND", errorCode(service.handle(new PublishRouteCommand(1L))));
        verify(routes, never()).save(any());
    }

    @Test
    void alreadyAssignedStudentIsAConflict() {
        when(routes.findById(1L)).thenReturn(Optional.of(route(List.of(new RouteAssignment(5L, 10L, new StudentId(7L))))));
        assertEquals("ROUTE_CONFLICT", errorCode(service.handle(new AssignStudentToRouteCommand(1L, 10L, 7L))));
        verify(routes, never()).save(any());
    }

    @Test
    void incompleteRouteCannotBePublished() {
        when(routes.findById(1L)).thenReturn(Optional.of(route(List.of())));
        assertEquals("BUSINESS_RULE_VIOLATION", errorCode(service.handle(new PublishRouteCommand(1L))));
        verify(routes, never()).save(any());
    }

    @Test
    void completeRouteIsPublishedAndSaved() {
        var route = route(List.of(new RouteAssignment(5L, 10L, new StudentId(7L))));
        when(routes.findById(1L)).thenReturn(Optional.of(route));
        when(routes.save(route)).thenReturn(route);
        var result = service.handle(new PublishRouteCommand(1L));
        assertEquals(1L, assertInstanceOf(Result.Success.class, result).value());
        assertTrue(route.isPublished());
    }

    @Test
    void deletingAMissingRouteIsNotFound() {
        when(routes.existsById(1L)).thenReturn(false);
        assertEquals("ROUTE_NOT_FOUND", errorCode(service.handle(new DeleteRouteCommand(1L))));
        verify(routes, never()).deleteById(any());
    }

    private static String errorCode(Result<Long, ApplicationError> result) {
        var failure = assertInstanceOf(Result.Failure.class, result);
        return ((ApplicationError) failure.error()).code();
    }
}
