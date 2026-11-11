package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** Child row: persists history; never has a standalone aggregate repository or pair-wide unique constraint. */
@Getter
@Setter
@Entity
@Table(name = "tutor_student_relationships")
public class TutorStudentRelationshipPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentPersistenceEntity student;

    @Column(name = "tutor_profile_id", nullable = false)
    private Long tutorProfileId;

    @Column(name = "authorized_by_profile_id", nullable = false)
    private Long authorizedByProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false, length = 14)
    private RelationshipType relationshipType;

    @Enumerated(EnumType.STRING)
    @Column(name = "authorization_status", nullable = false, length = 10)
    private AuthorizationStatus authorizationStatus;

    @Column(name = "authorized_at", nullable = false)
    private Instant authorizedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;
}
