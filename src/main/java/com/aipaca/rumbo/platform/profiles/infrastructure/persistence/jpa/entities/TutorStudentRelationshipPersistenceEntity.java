package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** Stores a single relationship per tutor-student pair, including its revoked state. */
@Getter
@Setter
@Entity
@Table(name = "tutor_student_relationships", uniqueConstraints = @UniqueConstraint(
        name = "uk_tutor_student_relationships_tutor_student", columnNames = {"tutor_profile_id", "student_id"}))
public class TutorStudentRelationshipPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "tutor_profile_id", nullable = false)
    private Long tutorProfileId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "authorized_by_profile_id")
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

    public TutorStudentRelationshipPersistenceEntity() {
    }
}
