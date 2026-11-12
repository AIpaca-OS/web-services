package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.DelayStatus;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.converters.ProfileIdPersistenceConverter;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.converters.TripIdPersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for delays. {@code previous_delay_id} is a self-referencing foreign key
 * to the delay that this one updates.
 */
@Entity
@Table(name = "delays")
@Getter
@Setter
@NoArgsConstructor
public class DelayPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = TripIdPersistenceConverter.class)
    @Column(name = "trip_id", nullable = false)
    private TripId tripId;

    @Convert(converter = ProfileIdPersistenceConverter.class)
    @Column(name = "reported_by_profile_id", nullable = false)
    private ProfileId reportedByProfileId;

    @Column(nullable = false, length = 240)
    private String cause;

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DelayStatus status;

    @Column(name = "previous_delay_id")
    private Long previousDelayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_delay_id", insertable = false, updatable = false)
    private DelayPersistenceEntity previousDelay;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;
}
