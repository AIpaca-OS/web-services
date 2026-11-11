package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationPersistenceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private NotificationType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private NotificationPriority priority;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, length = 2000)
    private String message;
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;
    @Column(name = "read_at")
    private Instant readAt;
    @Column(name = "route_id")
    private UUID routeId;
    @Column(name = "student_id")
    private Long studentId;

    public Long getId() { return id; }
    public void setId(Long value) { id=value; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType value) { type=value; }
    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority value) { priority=value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { title=value; }
    public String getMessage() { return message; }
    public void setMessage(String value) { message=value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { createdAt=value; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant value) { readAt=value; }
    public UUID getRouteId() { return routeId; }
    public void setRouteId(UUID value) { routeId=value; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long value) { studentId=value; }
}
