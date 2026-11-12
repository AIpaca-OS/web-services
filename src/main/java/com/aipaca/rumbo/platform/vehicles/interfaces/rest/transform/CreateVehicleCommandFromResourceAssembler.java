package com.aipaca.rumbo.platform.vehicles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.vehicles.domain.model.commands.CreateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources.CreateVehicleResource;

/**
 * Assembler to convert a CreateVehicleResource to a CreateVehicleCommand.
 */
public class CreateVehicleCommandFromResourceAssembler {
    public static CreateVehicleCommand toCommandFromResource(CreateVehicleResource resource) {
        return new CreateVehicleCommand(resource.driverProfileId(), resource.brand(), resource.model(), resource.licensePlate(),
                resource.capacity(), resource.year());
    }
}
