package com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.converters.StudentIdPersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for trip passengers. {@code trip_id} is a foreign key to {@code trips}.
 */
@Entity
@Table(name = "trip_passengers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trip_id", "student_id"}))
@Getter
@Setter
@NoArgsConstructor
public class TripPassengerPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private TripPersistenceEntity trip;

    @Convert(converter = StudentIdPersistenceConverter.class)
    @Column(name = "student_id", nullable = false)
    private StudentId studentId;

    @Column(name = "stop_id", nullable = false)
    private Long stopId;

    @Column(name = "stop_position", nullable = false)
    private int stopPosition;

    @Column(name = "stop_address", nullable = false, length = 350)
    private String stopAddress;
    @Column(nullable = false)
    private boolean absent;

    @Column(name = "stop_served", nullable = false)
    private boolean stopServed;

    @Column(name = "arrived_at")
    private Instant arrivedAt;
}
