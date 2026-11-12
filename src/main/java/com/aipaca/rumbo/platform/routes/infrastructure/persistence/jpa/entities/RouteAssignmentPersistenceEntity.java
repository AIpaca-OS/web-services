package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.converters.StudentIdPersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for route assignments.
 *
 * <p>{@code route_id} and {@code stop_id} are foreign keys inside the routes context.
 * {@code student_id} stores the converted identity of a student of another context.</p>
 */
@Entity
@Table(name = "route_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "student_id"}))
@Getter
@Setter
@NoArgsConstructor
public class RouteAssignmentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private RoutePersistenceEntity route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false)
    private RouteStopPersistenceEntity stop;

    @Convert(converter = StudentIdPersistenceConverter.class)
    @Column(name = "student_id", nullable = false)
    private StudentId studentId;
}
