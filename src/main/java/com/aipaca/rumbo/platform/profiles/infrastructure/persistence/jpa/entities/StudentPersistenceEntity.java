package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Aggregate root persistence entity; all tutor authorization history belongs to this root. */
@Getter
@Setter
@Entity
@Table(name = "students")
public class StudentPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Embedded
    private PersonNamePersistenceEmbeddable name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StudentStatus status;

    @Column(name = "anonymized_at")
    private Instant anonymizedAt;

    /** Optimistic locking protects concurrent authorization changes. */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /** Dirty root field on tutor-grant/revoke, even when child collection membership stays unchanged. */
    @Column(name = "authorization_revision", nullable = false)
    private long authorizationRevision;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            orphanRemoval = false)
    private List<TutorStudentRelationshipPersistenceEntity> tutorRelationships = new ArrayList<>();

    public void addTutorRelationship(TutorStudentRelationshipPersistenceEntity relationship) {
        relationship.setStudent(this);
        tutorRelationships.add(relationship);
    }
}
