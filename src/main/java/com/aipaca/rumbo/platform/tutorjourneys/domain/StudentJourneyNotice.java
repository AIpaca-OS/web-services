package com.aipaca.rumbo.platform.tutorjourneys.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rp_student_journey_notices",
        uniqueConstraints = @UniqueConstraint(name = "uq_student_journey_event", columnNames = "event_key"),
        indexes = @Index(name = "ix_student_journey_notices_student", columnList = "student_id"))
public class StudentJourneyNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false, length = 100)
    private String studentId;
    @Column(name = "route_id", nullable = false)
    private UUID routeId;
    @Column(name = "trip_id")
    private UUID tripId;
    @Column(name = "event_key", nullable = false, unique = true, length = 220)
    private String eventKey;
    @Column(nullable = false, length = 40)
    private String kind;
    @Column(nullable = false, length = 160)
    private String title;
    @Column(nullable = false, length = 800)
    private String message;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "read_at")
    private Instant readAt;

    protected StudentJourneyNotice() { }
    public StudentJourneyNotice(String studentId, UUID routeId, UUID tripId,
                                String eventKey, String kind, String title, String message) {
        this.studentId = studentId;
        this.routeId = routeId;
        this.tripId = tripId;
        this.eventKey = eventKey;
        this.kind = kind;
        this.title = title;
        this.message = message;
        this.createdAt = Instant.now();
    }
    public Long getId() { return id; }
    public String getStudentId() { return studentId; }
    public UUID getRouteId() { return routeId; }
    public UUID getTripId() { return tripId; }
    public String getKind() { return kind; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isRead() { return readAt != null; }
    public void markAsRead() { if (readAt == null) readAt = Instant.now(); }
}
