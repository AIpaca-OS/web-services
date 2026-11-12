package com.aipaca.rumbo.platform.vehicles.domain.model.commands;

/**
 * Delete Vehicle Command
 */
public record DeleteVehicleCommand(Long vehicleId) {
    public DeleteVehicleCommand {
        if (vehicleId == null || vehicleId < 1) {
            throw new IllegalArgumentException("Vehicle id cannot be null or less than 1");
        }
    }
}
