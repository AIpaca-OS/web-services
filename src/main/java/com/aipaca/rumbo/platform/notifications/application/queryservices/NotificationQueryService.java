package com.aipaca.rumbo.platform.notifications.application.queryservices;

import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsByRecipientProfileIdQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsByStudentIdQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetNotificationByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Notification Query Service
 */
public interface NotificationQueryService {
    Optional<Notification> handle(GetNotificationByIdQuery query);

    List<Notification> handle(GetAllNotificationsQuery query);

    List<Notification> handle(GetAllNotificationsByStudentIdQuery query);

    List<Notification> handle(GetAllNotificationsByRecipientProfileIdQuery query);
}
