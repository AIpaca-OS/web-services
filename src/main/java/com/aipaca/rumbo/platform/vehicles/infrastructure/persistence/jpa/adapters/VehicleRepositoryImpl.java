package com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.LicensePlate;
import com.aipaca.rumbo.platform.vehicles.domain.repositories.VehicleRepository;
import com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.assemblers.VehiclePersistenceAssembler;
import com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.repositories.VehiclePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain vehicle repository port with Spring Data JPA.
 */
@Repository
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehiclePersistenceRepository vehiclePersistenceRepository;

    public VehicleRepositoryImpl(VehiclePersistenceRepository vehiclePersistenceRepository) {
        this.vehiclePersistenceRepository = vehiclePersistenceRepository;
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehiclePersistenceRepository.findById(id).map(VehiclePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehiclePersistenceRepository.findAllByOrderByIdAsc().stream()
                .map(VehiclePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Vehicle> findAllByDriverProfileId(DriverProfileId driverProfileId) {
        return vehiclePersistenceRepository.findAllByDriverProfileIdOrderByIdAsc(driverProfileId).stream()
                .map(VehiclePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        var savedEntity = vehiclePersistenceRepository.save(VehiclePersistenceAssembler.toPersistenceFromDomain(vehicle));
        return VehiclePersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public boolean existsById(Long id) {
        return vehiclePersistenceRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        vehiclePersistenceRepository.deleteById(id);
    }

    @Override
    public boolean existsByLicensePlate(LicensePlate licensePlate) {
        return vehiclePersistenceRepository.existsByLicensePlate(licensePlate);
    }

    @Override
    public boolean existsByLicensePlateAndIdIsNot(LicensePlate licensePlate, Long id) {
        return vehiclePersistenceRepository.existsByLicensePlateAndIdIsNot(licensePlate, id);
    }
}
