package com.aipaca.rumbo.platform.vehicles.domain.model.commands;

/**
 * Update Vehicle Command
 */
public record UpdateVehicleCommand(Long vehicleId, String brand, String model, String licensePlate,
                                   Integer capacity, Integer year) {
    public UpdateVehicleCommand {
        if (vehicleId == null || vehicleId < 1) {
            throw new IllegalArgumentException("Vehicle id cannot be null or less than 1");
        }
    }
}
