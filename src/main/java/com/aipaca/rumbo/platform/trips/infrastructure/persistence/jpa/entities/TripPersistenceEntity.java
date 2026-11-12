package com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteShift;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.TripStatus;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.VehicleId;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.converters.DriverProfileIdPersistenceConverter;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.converters.RouteIdPersistenceConverter;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.converters.VehicleIdPersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for trips. Only one trip per route and service date is allowed.
 */
@Entity
@Table(name = "trips", uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "service_date"}))
@Getter
@Setter
@NoArgsConstructor
public class TripPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = RouteIdPersistenceConverter.class)
    @Column(name = "route_id", nullable = false)
    private RouteId routeId;

    @Convert(converter = DriverProfileIdPersistenceConverter.class)
    @Column(name = "driver_profile_id", nullable = false)
    private DriverProfileId driverProfileId;

    @Convert(converter = VehicleIdPersistenceConverter.class)
    @Column(name = "vehicle_id", nullable = false)
    private VehicleId vehicleId;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TripStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RouteShift shift;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopPosition ASC")
    private List<TripPassengerPersistenceEntity> passengers = new ArrayList<>();
}
