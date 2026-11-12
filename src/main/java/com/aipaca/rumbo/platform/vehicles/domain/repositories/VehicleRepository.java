package com.aipaca.rumbo.platform.vehicles.domain.repositories;

import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;

import java.util.List;
import java.util.Optional;

/**
 * Vehicle repository port.
 */
public interface VehicleRepository {
    Optional<Vehicle> findById(Long id);

    List<Vehicle> findAll();

    List<Vehicle> findAllByDriverProfileId(DriverProfileId driverProfileId);

    Vehicle save(Vehicle vehicle);

    boolean existsById(Long id);

    void deleteById(Long id);

    boolean existsByLicensePlate(LicensePlate licensePlate);

    boolean existsByLicensePlateAndIdIsNot(LicensePlate licensePlate, Long id);
}
