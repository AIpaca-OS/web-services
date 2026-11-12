package com.aipaca.rumbo.platform.vehicles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.vehicles.domain.model.aggregates.Vehicle;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources.VehicleResource;

/**
 * Assembler to convert a Vehicle entity to a VehicleResource.
 */
public class VehicleResourceFromEntityAssembler {
    public static VehicleResource toResourceFromEntity(Vehicle entity) {
        return new VehicleResource(entity.getId(), entity.getDriverProfileIdValue(), entity.getBrand(), entity.getModel(),
                entity.getLicensePlateValue(), entity.getCapacity(), entity.getYear(), entity.getStatus());
    }
}
