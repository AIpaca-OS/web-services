package com.aipaca.rumbo.platform.trips.domain.model.queries;

/**
 * Get All Trips By Driver Profile ID Query
 *
 * @param driverProfileId driver profile id
 */
public record GetAllTripsByDriverProfileIdQuery(Long driverProfileId) {
    public GetAllTripsByDriverProfileIdQuery {
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Driver profile id cannot be null or less than 1");
        }
    }
}
