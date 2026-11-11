package com.aipaca.rumbo.platform.notifications.application.internal.queryservices;

import com.aipaca.rumbo.platform.notifications.application.queryservices.NotificationQueryService;
import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsByRecipientProfileIdQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsByStudentIdQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.aipaca.rumbo.platform.notifications.domain.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Notification Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }

    // inherited javadoc
    @Override
    public List<Notification> handle(GetAllNotificationsQuery query) {
        return notificationRepository.findAll();
    }

    // inherited javadoc
    @Override
    public List<Notification> handle(GetAllNotificationsByStudentIdQuery query) {
        return notificationRepository.findAllByStudentId(new StudentId(query.studentId()));
    }

    // inherited javadoc
    @Override
    public List<Notification> handle(GetAllNotificationsByRecipientProfileIdQuery query) {
        return notificationRepository.findAllByRecipientProfileId(new ProfileId(query.recipientProfileId()));
    }
}
