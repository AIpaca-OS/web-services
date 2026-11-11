package com.aipaca.rumbo.notifications.application.queryservices;
import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.notifications.domain.model.queries.GetAllNotificationsQuery;

import java.util.List;

public interface NotificationQueryService {

    List<Notification> handle(GetAllNotificationsQuery query);
}