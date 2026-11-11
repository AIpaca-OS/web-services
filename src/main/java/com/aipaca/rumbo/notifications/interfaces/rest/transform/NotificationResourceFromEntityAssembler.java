package com.aipaca.rumbo.notifications.interfaces.rest.transform;
import com.aipaca.rumbo.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.notifications.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {

    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getId(),
                entity.getType(),
                entity.getPriority(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getReadAt(),
                entity.getRead(),
                entity.getRouteId() != null ? entity.getRouteId().value() : null,
                entity.getStudentId() != null ? entity.getStudentId().value() : null
        );
    }
}
