package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentStatus;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.converters.ProfileIdPersistenceConverter;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.converters.StudentIdPersistenceConverter;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.converters.TripIdPersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for incidents.
 */
@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
public class IncidentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = TripIdPersistenceConverter.class)
    @Column(name = "trip_id", nullable = false)
    private TripId tripId;

    @Convert(converter = ProfileIdPersistenceConverter.class)
    @Column(name = "reported_by_profile_id", nullable = false)
    private ProfileId reportedByProfileId;

    @Convert(converter = StudentIdPersistenceConverter.class)
    @Column(name = "student_id")
    private StudentId studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentCategory category;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IncidentStatus status;

    @Column(length = 500)
    private String resolution;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("acknowledgedAt ASC")
    private List<IncidentAcknowledgementPersistenceEntity> acknowledgements = new ArrayList<>();
}
