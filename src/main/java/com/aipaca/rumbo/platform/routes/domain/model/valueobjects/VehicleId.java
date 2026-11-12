package com.aipaca.rumbo.platform.routes.domain.model.valueobjects;

/**
 * Value object representing the vehicle id.
 *
 * <p>Links a route to a vehicle of the Vehicles bounded context.</p>
 *
 * @param vehicleId The vehicle id. It cannot be null or less than 1.
 */
public record VehicleId(Long vehicleId) {
    /**
     * Compact constructor for VehicleId.
     *
     * @throws IllegalArgumentException if the vehicle id is null or less than 1.
     */
    public VehicleId {
        if (vehicleId == null || vehicleId < 1) {
            throw new IllegalArgumentException("Vehicle id cannot be null or less than 1");
        }
    }
}
