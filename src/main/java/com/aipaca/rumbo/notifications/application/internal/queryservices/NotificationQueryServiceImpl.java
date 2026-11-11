package com.aipaca.rumbo.notifications.application.internal.queryservices;
import com.aipaca.rumbo.notifications.application.queryservices.NotificationQueryService;
import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.aipaca.rumbo.notifications.domain.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> handle(GetAllNotificationsQuery query) {
        return notificationRepository.findAll();
    }
}