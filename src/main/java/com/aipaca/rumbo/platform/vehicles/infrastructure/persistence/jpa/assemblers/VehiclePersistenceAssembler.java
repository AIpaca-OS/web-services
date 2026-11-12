package com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;

/**
 * Static assembler between vehicle domain and persistence representations.
 */
public final class VehiclePersistenceAssembler {

    private VehiclePersistenceAssembler() {
    }

    public static Vehicle toDomainFromPersistence(VehiclePersistenceEntity entity) {
        if (entity == null) return null;
        return Vehicle.reconstitute(entity.getId(), entity.getDriverProfileId(), entity.getBrand(), entity.getModel(), entity.getLicensePlate(),
                entity.getCapacity(), entity.getYear(), entity.getStatus());
    }

    public static VehiclePersistenceEntity toPersistenceFromDomain(Vehicle vehicle) {
        if (vehicle == null) return null;
        var entity = new VehiclePersistenceEntity();
        if (vehicle.getId() != null) {
            entity.setId(vehicle.getId());
        }
        entity.setDriverProfileId(vehicle.getDriverProfileId());
        entity.setBrand(vehicle.getBrand());
        entity.setModel(vehicle.getModel());
        entity.setLicensePlate(vehicle.getLicensePlate());
        entity.setCapacity(vehicle.getCapacity());
        entity.setYear(vehicle.getYear());
        entity.setStatus(vehicle.getStatus());
        return entity;
    }
}
