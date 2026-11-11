package com.aipaca.rumbo.platform.vehicles.domain.model.queries;

/**
 * Get All Vehicles By Driver Profile ID Query
 *
 * @param driverProfileId driver profile id
 */
public record GetAllVehiclesByDriverProfileIdQuery(Long driverProfileId) {
    public GetAllVehiclesByDriverProfileIdQuery {
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Driver profile id cannot be null or less than 1");
        }
    }
}
