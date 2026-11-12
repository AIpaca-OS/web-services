package com.aipaca.rumbo.notifications.interfaces.rest;

import com.aipaca.rumbo.notifications.application.internal.NotificationService;
import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.routes.domain.model.Route;
import com.aipaca.rumbo.routes.domain.model.RouteShift;
import com.aipaca.rumbo.routes.domain.model.RouteStatus;
import com.aipaca.rumbo.routes.domain.repositories.RouteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DemoDriverAlertsControllerTests {
    @Test void refusesNoticesForUnpublishedRoutes() {
        var repo = mock(RouteRepository.class);
        var notifier = mock(NotificationService.class);
        var route = new Route("Test", RouteShift.MORNING, "vehicle-1");
        when(repo.findById(route.getId())).thenReturn(Optional.of(route));

        var controller = new DemoDriverAlertsController(repo, notifier);
        var ex = assertThrows(ResponseStatusException.class,
                () -> controller.create(route.getId(),
                        new DemoDriverAlertsController.DemoAlertRequest("DELAY", "Traffic delay")));
        assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
        verify(notifier, never()).create(any());
    }

    @Test void refusesUnknownRoute() {
        var repo = mock(RouteRepository.class);
        var notifier = mock(NotificationService.class);
        var controller = new DemoDriverAlertsController(repo, notifier);
        var ex = assertThrows(ResponseStatusException.class,
                () -> controller.create(UUID.randomUUID(),
                        new DemoDriverAlertsController.DemoAlertRequest("DELAY", "Traffic delay")));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getStatusCode().value());
    }

    @Test void rejectsInvalidNotificationKinds() {
        var repo = mock(RouteRepository.class);
        var notifier = mock(NotificationService.class);
        var route = mock(Route.class);
        var id = UUID.randomUUID();
        when(route.getStatus()).thenReturn(RouteStatus.PUBLISHED);
        when(repo.findById(id)).thenReturn(Optional.of(route));
        var controller = new DemoDriverAlertsController(repo, notifier);
        var ex = assertThrows(ResponseStatusException.class,
                () -> controller.create(id,
                        new DemoDriverAlertsController.DemoAlertRequest("OTHER", "Test notice")));
        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
        verify(notifier, never()).create(any());
    }

    @Test void persistsRouteNoticeWithNoStudentId() {
        var repo = mock(RouteRepository.class);
        var notifier = mock(NotificationService.class);
        var route = mock(Route.class);
        var id = UUID.randomUUID();
        when(route.getStatus()).thenReturn(RouteStatus.PUBLISHED);
        when(route.getCode()).thenReturn("R-TEST123");
        when(repo.findById(id)).thenReturn(Optional.of(route));
        when(notifier.create(any(CreateNotificationCommand.class))).thenAnswer(
                invocation -> new Notification(invocation.getArgument(0)));
        var controller = new DemoDriverAlertsController(repo, notifier);

        var created = controller.create(id,
                new DemoDriverAlertsController.DemoAlertRequest("INCIDENT", "Mechanical issue, no personal data"));
        assertTrue(created.title().contains("Incidencia"));
        var captor = org.mockito.ArgumentCaptor.forClass(CreateNotificationCommand.class);
        verify(notifier).create(captor.capture());
        assertEquals(id, captor.getValue().routeId());
        assertNull(captor.getValue().studentId());
    }
}
