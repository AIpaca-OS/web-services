package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for route stops. {@code route_id} is a foreign key to {@code routes}.
 */
@Entity
@Table(name = "route_stops")
@Getter
@Setter
@NoArgsConstructor
public class RouteStopPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private RoutePersistenceEntity route;

    @Column(nullable = false, length = 350)
    private String address;
    @Column(nullable = false)
    private int position;
}
