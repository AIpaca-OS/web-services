package com.aipaca.rumbo.platform.notifications.application.commandservices;

import com.aipaca.rumbo.platform.notifications.domain.model.commands.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Notification Command Service. Every handler returns the id of the affected notification.
 */
public interface NotificationCommandService {
    Result<Long, ApplicationError> handle(CreateNotificationCommand command);

    Result<Long, ApplicationError> handle(CreateStudentJourneyNotificationCommand command);

    Result<Long, ApplicationError> handle(MarkNotificationAsReadCommand command);

    Result<Long, ApplicationError> handle(DeleteNotificationCommand command);
}
