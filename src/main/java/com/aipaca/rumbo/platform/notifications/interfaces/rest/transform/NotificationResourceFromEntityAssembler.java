package com.aipaca.rumbo.platform.notifications.interfaces.rest.transform;

import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.resources.NotificationResource;

/**
 * Assembler to convert a Notification entity to a NotificationResource.
 */
public class NotificationResourceFromEntityAssembler {
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(entity.getId(), entity.getCategory(), entity.getType(), entity.getPriority(), entity.getTitle(),
                entity.getMessage(), entity.getCreatedAt(), entity.getReadAt(), entity.isRead(),
                entity.getRouteIdValue(), entity.getTripIdValue(), entity.getStudentIdValue(),
                entity.getRecipientProfileIdValue(),
                entity.getJourneyEvent());
    }
}
