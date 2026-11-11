package com.aipaca.rumbo.platform.notifications.domain.model.aggregates;

import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateStudentJourneyNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationCategory;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTests {

    @Test
    void notificationRequiresTitleAndMessage() {
        assertThrows(IllegalArgumentException.class, () -> new Notification(new CreateNotificationCommand(
                NotificationType.INFO, NotificationPriority.REGULAR, " ", "Mensaje", null, null, null)));
        assertThrows(IllegalArgumentException.class, () -> new Notification(new CreateNotificationCommand(
                NotificationType.INFO, NotificationPriority.REGULAR, "Título", null, null, null, null)));
    }

    @Test
    void markAsReadKeepsTheFirstReadDate() {
        var notification = new Notification(new CreateNotificationCommand(
                NotificationType.INFO, NotificationPriority.REGULAR, "Título", "Mensaje", 1L, 2L, 9L));
        assertFalse(notification.isRead());
        notification.markAsRead();
        var firstRead = notification.getReadAt();
        notification.markAsRead();
        assertEquals(firstRead, notification.getReadAt());
        assertEquals(1L, notification.getRouteIdValue());
        assertEquals(2L, notification.getStudentIdValue());
        assertEquals(9L, notification.getRecipientProfileIdValue());
    }

    @Test
    void studentJourneyNotificationKeepsIdentitiesAndFollowsTheJourneyFact() {
        var arrived = new Notification(new CreateStudentJourneyNotificationCommand(
                5L, 2L, 7L, JourneyEvent.STUDENT_ARRIVED, "Llegada confirmada", "El estudiante llegó.", 4L));
        assertEquals(NotificationCategory.STUDENT_JOURNEY, arrived.getCategory());
        assertEquals(5L, arrived.getStudentIdValue());
        assertEquals(2L, arrived.getRouteIdValue());
        assertEquals(7L, arrived.getTripIdValue());
        assertEquals(4L, arrived.getRecipientProfileIdValue());
        assertEquals(NotificationType.SUCCESS, arrived.getType());
        var cancelled = new Notification(new CreateStudentJourneyNotificationCommand(
                5L, 2L, 7L, JourneyEvent.TRIP_CANCELLED, "Viaje cancelado", "El conductor canceló el recorrido.", null));
        assertEquals(NotificationPriority.CRITICAL, cancelled.getPriority());
        var published = new Notification(new CreateStudentJourneyNotificationCommand(
                5L, 2L, null, JourneyEvent.ROUTE_PUBLISHED, "Ruta publicada", "Ya está disponible.", null));
        assertNull(published.getTripIdValue());
        assertEquals(NotificationType.INFO, published.getType());
    }

    @Test
    void studentJourneyNotificationRequiresStudentRouteAndJourneyFact() {
        assertThrows(IllegalArgumentException.class, () -> new CreateStudentJourneyNotificationCommand(
                0L, 2L, null, JourneyEvent.ABSENT, "Ausencia", "Mensaje", null));
        assertThrows(IllegalArgumentException.class, () -> new CreateStudentJourneyNotificationCommand(
                5L, null, null, JourneyEvent.ABSENT, "Ausencia", "Mensaje", null));
        assertThrows(IllegalArgumentException.class, () -> new CreateStudentJourneyNotificationCommand(
                5L, 2L, null, null, "Ausencia", "Mensaje", null));
        assertThrows(IllegalArgumentException.class, () -> new CreateStudentJourneyNotificationCommand(
                5L, 2L, null, JourneyEvent.ABSENT, "Ausencia", "Mensaje", 0L));
    }

    @Test
    void generalNotificationHasGeneralCategory() {
        var notification = new Notification(new CreateNotificationCommand(
                NotificationType.INFO, NotificationPriority.REGULAR, "Título", "Mensaje", null, null, null));
        assertEquals(NotificationCategory.GENERAL, notification.getCategory());
    }
}
