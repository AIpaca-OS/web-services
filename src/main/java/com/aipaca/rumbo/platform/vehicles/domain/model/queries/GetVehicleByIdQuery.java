package com.aipaca.rumbo.platform.vehicles.domain.model.queries;

/**
 * Get Vehicle By ID Query
 */
public record GetVehicleByIdQuery(Long vehicleId) {
    public GetVehicleByIdQuery {
        if (vehicleId == null || vehicleId < 1) {
            throw new IllegalArgumentException("Vehicle id cannot be null or less than 1");
        }
    }
}
