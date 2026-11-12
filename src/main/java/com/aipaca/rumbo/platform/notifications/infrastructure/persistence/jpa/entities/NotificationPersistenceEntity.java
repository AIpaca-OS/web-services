package com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationCategory;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.converters.ProfileIdPersistenceConverter;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.converters.RouteIdPersistenceConverter;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.converters.StudentIdPersistenceConverter;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.converters.TripIdPersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for notifications.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class NotificationPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPriority priority;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String message;

    @Convert(converter = RouteIdPersistenceConverter.class)
    @Column(name = "route_id")
    private RouteId routeId;

    @Convert(converter = TripIdPersistenceConverter.class)
    @Column(name = "trip_id")
    private TripId tripId;

    @Convert(converter = StudentIdPersistenceConverter.class)
    @Column(name = "student_id")
    private StudentId studentId;

    @Convert(converter = ProfileIdPersistenceConverter.class)
    @Column(name = "recipient_profile_id")
    private ProfileId recipientProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "journey_event", length = 20)
    private JourneyEvent journeyEvent;

    @Column(name = "read_at")
    private Instant readAt;
}
