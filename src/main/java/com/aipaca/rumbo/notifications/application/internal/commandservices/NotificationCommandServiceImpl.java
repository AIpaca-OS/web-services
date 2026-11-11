package com.aipaca.rumbo.notifications.application.internal.commandservices;
import com.aipaca.rumbo.notifications.application.commandservices.NotificationCommandService;
import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.notifications.domain.model.commands.DeleteNotificationCommand;
import com.aipaca.rumbo.notifications.domain.repositories.NotificationRepository;
import com.aipaca.rumbo.shared.application.result.ApplicationError;
import com.aipaca.rumbo.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Notification Command Service Implementation
 */
@Service
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
    public Result<Notification, ApplicationError> handle(CreateNotificationCommand command) {
        try {
            var notification = new Notification(command);
            var savedNotification = notificationRepository.save(notification);
            return Result.success(savedNotification);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Notification", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(
                    "Notification creation",
                    e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Boolean, ApplicationError> handle(DeleteNotificationCommand command) {
        try {
            var notification = notificationRepository.findById(command.notificationId());

            if (notification.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "Notification",
                        "Notification with id '%s' was not found".formatted(command.notificationId())));
            }

            notificationRepository.deleteById(command.notificationId());
            return Result.success(true);

        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(
                    "Notification deletion",
                    e.getMessage()));
        }
    }
}