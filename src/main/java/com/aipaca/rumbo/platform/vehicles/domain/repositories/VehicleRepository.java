package com.aipaca.rumbo.platform.vehicles.domain.repositories;

import com.aipaca.rumbo.platform.vehicles.domain.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByLicensePlateIgnoreCase(String licensePlate);
    boolean existsByLicensePlateIgnoreCaseAndIdNot(String licensePlate, Long id);
}
