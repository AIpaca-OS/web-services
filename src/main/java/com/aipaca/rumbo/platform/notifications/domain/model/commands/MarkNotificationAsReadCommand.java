package com.aipaca.rumbo.platform.notifications.domain.model.commands;

/**
 * Mark Notification As Read Command
 *
 * @param notificationId notification id
 */
public record MarkNotificationAsReadCommand(Long notificationId) {
    public MarkNotificationAsReadCommand {
        if (notificationId == null || notificationId < 1) {
            throw new IllegalArgumentException("Notification id cannot be null or less than 1");
        }
    }
}
