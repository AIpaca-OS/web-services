package com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for vehicle persistence entities.
 */
@Repository
public interface VehiclePersistenceRepository extends JpaRepository<VehiclePersistenceEntity, Long> {
    List<VehiclePersistenceEntity> findAllByOrderByIdAsc();

    List<VehiclePersistenceEntity> findAllByDriverProfileIdOrderByIdAsc(DriverProfileId driverProfileId);

    boolean existsByLicensePlate(LicensePlate licensePlate);

    boolean existsByLicensePlateAndIdIsNot(LicensePlate licensePlate, Long id);
}
