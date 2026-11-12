package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** Request audit trail; persisting an APPROVED request alone does not remove personal data. */
@Getter
@Setter
@Entity
@Table(name = "data_deletion_requests")
public class DataDeletionRequestPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "requested_by_profile_id", nullable = false)
    private Long requestedByProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private DeletionRequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolution_note", length = 500)
    private String resolutionNote;
}
