package com.aipaca.rumbo.platform.notifications.domain.model.queries;

/**
 * Get Notification By ID Query
 *
 * @param notificationId notification id
 */
public record GetNotificationByIdQuery(Long notificationId) {
    public GetNotificationByIdQuery {
        if (notificationId == null || notificationId < 1) {
            throw new IllegalArgumentException("Notification id cannot be null or less than 1");
        }
    }
}
