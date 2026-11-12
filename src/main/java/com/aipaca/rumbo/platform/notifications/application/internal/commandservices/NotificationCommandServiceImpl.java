package com.aipaca.rumbo.platform.notifications.application.internal.commandservices;

import com.aipaca.rumbo.platform.notifications.application.commandservices.NotificationCommandService;
import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.*;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.notifications.domain.repositories.NotificationRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notification Command Service Implementation
 */
@Service
@Transactional
public class NotificationCommandServiceImpl implements NotificationCommandService {
    private final NotificationRepository notificationRepository;

    /**
     * Constructor
     *
     * @param notificationRepository The {@link NotificationRepository} instance
     */
    public NotificationCommandServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CreateNotificationCommand command) {
        try {
            var savedNotification = notificationRepository.save(new Notification(command));
            return Result.success(savedNotification.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Notification", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-notification", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CreateStudentJourneyNotificationCommand command) {
        var recipientProfileId = command.recipientProfileId() == null
                ? null : new ProfileId(command.recipientProfileId());
        if (command.tripId() != null && notificationRepository.existsByStudentIdAndTripIdAndJourneyEventAndRecipientProfileId(
                new StudentId(command.studentId()), new TripId(command.tripId()), command.journeyEvent(), recipientProfileId)) {
            return Result.failure(ApplicationError.conflict("Notification",
                    "The student already has a %s notification for trip %d".formatted(command.journeyEvent(), command.tripId())));
        }
        try {
            var savedNotification = notificationRepository.save(new Notification(command));
            return Result.success(savedNotification.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Student journey notification", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-student-journey-notification", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(MarkNotificationAsReadCommand command) {
        var result = notificationRepository.findById(command.notificationId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Notification", command.notificationId().toString()));
        try {
            var notification = result.get();
            notification.markAsRead();
            return Result.success(notificationRepository.save(notification).getId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("mark-notification-as-read", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(DeleteNotificationCommand command) {
        if (!notificationRepository.existsById(command.notificationId()))
            return Result.failure(ApplicationError.notFound("Notification", command.notificationId().toString()));
        try {
            notificationRepository.deleteById(command.notificationId());
            return Result.success(command.notificationId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-notification", e.getMessage()));
        }
    }
}
