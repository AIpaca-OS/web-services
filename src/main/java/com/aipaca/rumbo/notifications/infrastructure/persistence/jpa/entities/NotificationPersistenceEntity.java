package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.entities;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.converters.StudentIdPersistenceConverter;
import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.converters.RouteIdPersistenceConverter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class NotificationPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String readAt;

    @Column(nullable = false)
    private boolean read;

    @Convert(converter = RouteIdPersistenceConverter.class)
    @Column(name = "route_id", nullable = false)
    private RouteId routeId;

    @Convert(converter = StudentIdPersistenceConverter.class)
    @Column(name = "student_id", nullable = false)
    private StudentId studentId;

}
