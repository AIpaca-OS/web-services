package com.aipaca.rumbo.platform.notifications.domain.model;

import com.aipaca.rumbo.platform.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTests {
    @Test void markReadUpdatesState() {
        Notification n = new Notification(new CreateNotificationCommand(
                NotificationType.info, NotificationPriority.regular,
                "Demo", "General message", null, null));
        assertFalse(n.isRead());
        n.markAsRead();
        assertTrue(n.isRead());
        assertNotNull(n.getReadAt());
    }
    @Test void emptyMessageIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Notification(
                new CreateNotificationCommand(NotificationType.info, NotificationPriority.regular,
                        "Demo", "", null, null)));
    }
}
