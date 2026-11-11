package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;

/**
 * Static assembler between notification domain and persistence representations.
 */
public final class NotificationPersistenceAssembler {

    private NotificationPersistenceAssembler() {
    }

    public static Notification toDomainFromPersistence(NotificationPersistenceEntity entity) {
        if (entity == null) return null;

        return Notification.reconstitute(
                entity.getId(),
                entity.getType(),
                entity.getPriority(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getReadAt(),
                entity.getRead(),
                toDomainFromPersistence(entity.getRouteId()),
                toDomainFromPersistence(entity.getStudentId())
        );
    }

    public static NotificationPersistenceEntity toPersistenceFromDomain(Notification notification) {
        if (notification == null) return null;

        var entity = new NotificationPersistenceEntity();
        entity.setId(notification.getId());
        entity.setType(notification.getType());
        entity.setPriority(notification.getPriority());
        entity.setTitle(notification.getTitle());
        entity.setMessage(notification.getMessage());
        entity.setReadAt(notification.getReadAt());
        entity.setRead(notification.getRead());
        entity.setRouteId(toPersistenceFromDomain(notification.getRouteId()));
        entity.setStudentId(toPersistenceFromDomain(notification.getStudentId()));

        return entity;
    }

    private static RouteId toDomainFromPersistence(Long value) {
        return value == null ? null : new RouteId(value);
    }

    private static StudentId toDomainFromPersistence(Long value) {
        return value == null ? null : new StudentId(value);
    }

    private static Long toPersistenceFromDomain(RouteId value) {
        return value == null ? null : value.value();
    }

    private static Long toPersistenceFromDomain(StudentId value) {
        return value == null ? null : value.value();
    }
}