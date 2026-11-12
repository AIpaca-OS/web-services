package com.aipaca.rumbo.platform.notifications.domain.model.commands;

import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;

/**
 * Create Notification Command
 *
 * @param type      notification type
 * @param priority  notification priority
 * @param title     notification title
 * @param message   notification message
 * @param routeId   related route id, or null
 * @param studentId related student id, or null
 * @param recipientProfileId recipient profile id, or null for a notice to every user
 */
public record CreateNotificationCommand(NotificationType type, NotificationPriority priority, String title,
                                        String message, Long routeId, Long studentId, Long recipientProfileId) {
    public CreateNotificationCommand {
        if (type == null) throw new IllegalArgumentException("Notification type cannot be null");
        if (priority == null) throw new IllegalArgumentException("Notification priority cannot be null");
    }
}
