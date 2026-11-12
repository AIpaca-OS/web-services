package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteShift;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteStatus;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.VehicleId;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.converters.DriverProfileIdPersistenceConverter;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.converters.VehicleIdPersistenceConverter;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.embeddables.RouteSchedulePersistenceEmbeddable;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for routes.
 *
 * <p>Assignments are declared before stops so that, when a route is deleted,
 * the assignments that reference a stop are removed first.</p>
 */
@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
public class RoutePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = DriverProfileIdPersistenceConverter.class)
    @Column(name = "driver_profile_id", nullable = false)
    private DriverProfileId driverProfileId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RouteShift shift;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RouteStatus status;

    @Convert(converter = VehicleIdPersistenceConverter.class)
    @Column(name = "vehicle_id")
    private VehicleId vehicleId;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "schedule_confirmed", nullable = false)
    private boolean scheduleConfirmed;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteAssignmentPersistenceEntity> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<RouteStopPersistenceEntity> stops = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "route_schedules", joinColumns = @JoinColumn(name = "route_id"))
    private List<RouteSchedulePersistenceEmbeddable> schedule = new ArrayList<>();
}
