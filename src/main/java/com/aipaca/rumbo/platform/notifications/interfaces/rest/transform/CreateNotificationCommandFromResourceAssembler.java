package com.aipaca.rumbo.platform.notifications.interfaces.rest.transform;

import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.resources.CreateNotificationResource;

import java.util.Locale;

/**
 * Assembler to convert a CreateNotificationResource to a CreateNotificationCommand.
 */
public class CreateNotificationCommandFromResourceAssembler {
    /**
     * Converts a CreateNotificationResource to a CreateNotificationCommand.
     * Type and priority are accepted regardless of letter case.
     *
     * @param resource The {@link CreateNotificationResource} resource to convert.
     * @return The {@link CreateNotificationCommand} command.
     * @throws IllegalArgumentException if the type or the priority is not supported.
     */
    public static CreateNotificationCommand toCommandFromResource(CreateNotificationResource resource) {
        NotificationType type;
        NotificationPriority priority;
        try {
            type = NotificationType.valueOf(resource.type().trim().toUpperCase(Locale.ROOT));
            priority = NotificationPriority.valueOf(resource.priority().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid notification type or priority");
        }
        return new CreateNotificationCommand(type, priority, resource.title(), resource.message(),
                resource.routeId(), resource.studentId(), resource.recipientProfileId());
    }
}
