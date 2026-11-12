package com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;

/**
 * Static assembler between notification domain and persistence representations.
 */
public final class NotificationPersistenceAssembler {

    private NotificationPersistenceAssembler() {
    }

    public static Notification toDomainFromPersistence(NotificationPersistenceEntity entity) {
        if (entity == null) return null;
        return Notification.reconstitute(entity.getId(), entity.getCategory(), entity.getType(), entity.getPriority(),
                entity.getTitle(), entity.getMessage(), entity.getRouteId(), entity.getTripId(), entity.getStudentId(),
                entity.getRecipientProfileId(), entity.getJourneyEvent(),
                entity.getCreatedAt() == null ? null : entity.getCreatedAt().toInstant(), entity.getReadAt());
    }

    public static NotificationPersistenceEntity toPersistenceFromDomain(Notification notification) {
        if (notification == null) return null;
        var entity = new NotificationPersistenceEntity();
        if (notification.getId() != null) {
            entity.setId(notification.getId());
        }
        entity.setCategory(notification.getCategory());
        entity.setType(notification.getType());
        entity.setPriority(notification.getPriority());
        entity.setTitle(notification.getTitle());
        entity.setMessage(notification.getMessage());
        entity.setRouteId(notification.getRouteId());
        entity.setTripId(notification.getTripId());
        entity.setStudentId(notification.getStudentId());
        entity.setRecipientProfileId(notification.getRecipientProfileId());
        entity.setJourneyEvent(notification.getJourneyEvent());
        entity.setReadAt(notification.getReadAt());
        return entity;
    }
}
