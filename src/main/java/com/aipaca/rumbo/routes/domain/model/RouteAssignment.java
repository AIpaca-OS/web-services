package com.aipaca.rumbo.routes.domain.model;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
@Embeddable
public class RouteAssignment {
    @Column(name="assignment_id", nullable=false) private UUID id;
    @Column(name="stop_id", nullable=false) private UUID stopId;
    @Column(name="student_id", nullable=false, length=100) private String studentId;
    protected RouteAssignment() {}
    public RouteAssignment(UUID stopId, String studentId) {
        if (stopId == null || studentId == null || studentId.isBlank()) throw new IllegalArgumentException("Stop and student are required");
        id = UUID.randomUUID(); this.stopId = stopId; this.studentId = studentId.trim();
    }
    public UUID getId() { return id; }
    public UUID getStopId() { return stopId; }
    public String getStudentId() { return studentId; }
}
