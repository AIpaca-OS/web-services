package com.aipaca.rumbo.platform.notifications.domain.model.commands;

/**
 * Delete Notification Command
 *
 * @param notificationId notification id
 */
public record DeleteNotificationCommand(Long notificationId) {
    public DeleteNotificationCommand {
        if (notificationId == null || notificationId < 1) {
            throw new IllegalArgumentException("Notification id cannot be null or less than 1");
        }
    }
}
