package com.aipaca.rumbo.notifications.application.commandservices;
import com.aipaca.rumbo.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.notifications.domain.model.commands.DeleteNotificationCommand;
import com.aipaca.rumbo.shared.application.result.ApplicationError;
import com.aipaca.rumbo.shared.application.result.Result;

public interface NotificationCommandService {

    Result<Notification, ApplicationError> handle(CreateNotificationCommand command);

    Result<Boolean, ApplicationError> handle(DeleteNotificationCommand command);
}
