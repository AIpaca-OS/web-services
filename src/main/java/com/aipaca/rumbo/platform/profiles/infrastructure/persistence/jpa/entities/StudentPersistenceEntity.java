package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

/** Student persistence mapping; storing anonymizedAt does not perform anonymization. */
@Getter
@Setter
@Entity
@Table(name = "students")
public class StudentPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Embedded
    private PersonNamePersistenceEmbeddable name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "school_id")
    private Long schoolId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StudentStatus status;

    @Column(name = "anonymized_at")
    private Instant anonymizedAt;

    public StudentPersistenceEntity() {
    }
}
