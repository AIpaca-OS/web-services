package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Persistence representation of a student's enrollment in a driver's service.
 *
 * <p>ENDED rows remain in the table and do not prevent a later enrollment of the
 * same student-driver pair. Therefore a conventional composite UNIQUE constraint
 * on that pair would be incorrect; concurrency-safe active uniqueness requires
 * a conditional PostgreSQL index, to be reviewed before any schema migration.</p>
 */
@Getter
@Setter
@Entity
@Table(name = "service_enrollments")
public class ServiceEnrollmentPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "driver_profile_id", nullable = false)
    private Long driverProfileId;

    @Column(name = "requested_by_profile_id", nullable = false)
    private Long requestedByProfileId;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    public ServiceEnrollmentPersistenceEntity() {
    }
}
