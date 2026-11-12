package com.aipaca.rumbo.platform.vehicles.application.commandservices;

import com.aipaca.rumbo.platform.vehicles.domain.model.commands.CreateVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.DeleteVehicleCommand;
import com.aipaca.rumbo.platform.vehicles.domain.model.commands.UpdateVehicleCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Vehicle Command Service
 */
public interface VehicleCommandService {
    /**
     * Handle Create Vehicle Command
     *
     * @param command The {@link CreateVehicleCommand} Command
     * @return A {@link Result} containing the created vehicle id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(CreateVehicleCommand command);

    /**
     * Handle Update Vehicle Command
     *
     * @param command The {@link UpdateVehicleCommand} Command
     * @return A {@link Result} containing the updated vehicle id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(UpdateVehicleCommand command);

    /**
     * Handle Delete Vehicle Command
     *
     * @param command The {@link DeleteVehicleCommand} Command
     * @return A {@link Result} containing the deleted vehicle id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(DeleteVehicleCommand command);
}
