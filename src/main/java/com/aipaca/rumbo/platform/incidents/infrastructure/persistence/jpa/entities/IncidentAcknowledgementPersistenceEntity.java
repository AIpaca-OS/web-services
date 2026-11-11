package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.converters.ProfileIdPersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for incident acknowledgements. {@code incident_id} is a foreign key to
 * {@code incidents}, and a tutor acknowledges an incident only once.
 */
@Entity
@Table(name = "incident_acknowledgements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"incident_id", "tutor_profile_id"}))
@Getter
@Setter
@NoArgsConstructor
public class IncidentAcknowledgementPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private IncidentPersistenceEntity incident;

    @Convert(converter = ProfileIdPersistenceConverter.class)
    @Column(name = "tutor_profile_id", nullable = false)
    private ProfileId tutorProfileId;

    @Column(name = "acknowledged_at", nullable = false)
    private Instant acknowledgedAt;
}
