package com.aipaca.rumbo.platform.vehicles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.vehicles.domain.model.commands.UpdateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources.UpdateVehicleResource;

/**
 * Assembler to convert an UpdateVehicleResource to an UpdateVehicleCommand.
 */
public class UpdateVehicleCommandFromResourceAssembler {
    public static UpdateVehicleCommand toCommandFromResource(Long vehicleId, UpdateVehicleResource resource) {
        return new UpdateVehicleCommand(vehicleId, resource.brand(), resource.model(), resource.licensePlate(),
                resource.capacity(), resource.year());
    }
}
