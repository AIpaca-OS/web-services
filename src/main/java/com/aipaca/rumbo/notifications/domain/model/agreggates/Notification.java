package com.aipaca.rumbo.notifications.domain.model.agreggates;

import com.aipaca.rumbo.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.UUID;

/** General service notice. Student-specific notices wait for authenticated access. */
public class Notification extends AbstractDomainAggregateRoot<Notification> {
    private Long id;
    private NotificationType type;
    private NotificationPriority priority;
    private String title;
    private String message;
    private Instant createdAt;
    private Instant readAt;
    private UUID routeId;
    private Long studentId;

    public Notification(Long id, NotificationType type, NotificationPriority priority,
                        String title, String message, Instant createdAt, Instant readAt,
                        UUID routeId, Long studentId) {
        if (title == null || title.isBlank() || message == null || message.isBlank())
            throw new IllegalArgumentException("Title and message are required");
        this.id=id;this.type=type;this.priority=priority;
        this.title=title;this.message=message;
        this.createdAt=createdAt;this.readAt=readAt;
        this.routeId=routeId;this.studentId=studentId;
    }
    public Notification(CreateNotificationCommand command) {
        this(null, command.type(), command.priority(), command.title(), command.message(),
                Instant.now(), null, command.routeId(), command.studentId());
    }
    public void markAsRead() { if (readAt == null) readAt = Instant.now(); }
    public Long getId() { return id; }
    public NotificationType getType() { return type; }
    public NotificationPriority getPriority() { return priority; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getReadAt() { return readAt; }
    public boolean isRead() { return readAt != null; }
    public UUID getRouteId() { return routeId; }
    public Long getStudentId() { return studentId; }
}
