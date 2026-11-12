package com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.entities;

import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.VehicleStatus;
import com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.converters.DriverProfileIdPersistenceConverter;
import com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.converters.LicensePlatePersistenceConverter;
import com.aipaca.rumbo.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for vehicles.
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
public class VehiclePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = DriverProfileIdPersistenceConverter.class)
    @Column(name = "driver_profile_id", nullable = false)
    private DriverProfileId driverProfileId;

    @Column(nullable = false, length = 80)
    private String brand;

    @Column(nullable = false, length = 80)
    private String model;

    @Convert(converter = LicensePlatePersistenceConverter.class)
    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private LicensePlate licensePlate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private VehicleStatus status;
}
