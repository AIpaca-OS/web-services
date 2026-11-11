package com.aipaca.rumbo.notifications.interfaces.rest.transform;
import com.aipaca.rumbo.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.notifications.interfaces.rest.resources.CreateNotificationResource;

public class CreateNotificationCommandFromResourceAssembler {

    public static CreateNotificationCommand toCommandFromResource(CreateNotificationResource resource) {
        return new CreateNotificationCommand(
                resource.type(),
                resource.priority(),
                resource.title(),
                resource.message(),
                resource.routeId(),
                resource.studentId()
        );
    }
}
