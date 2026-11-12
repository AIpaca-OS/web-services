package com.aipaca.rumbo.platform.vehicles.domain.model.commands;

/**
 * Create Vehicle Command
 *
 * @param driverProfileId id of the driver profile that owns the vehicle
 * @param brand           vehicle brand
 * @param model           vehicle model
 * @param licensePlate    license plate
 * @param capacity        passenger capacity
 * @param year            manufacturing year
 */
public record CreateVehicleCommand(Long driverProfileId, String brand, String model, String licensePlate,
                                   Integer capacity, Integer year) {
    /**
     * Compact constructor for CreateVehicleCommand.
     *
     * @throws IllegalArgumentException if the driver profile id is invalid.
     */
    public CreateVehicleCommand {
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Driver profile id cannot be null or less than 1");
        }
    }
}
