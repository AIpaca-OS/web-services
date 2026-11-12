package com.aipaca.rumbo.platform.routes.domain.model.queries;

/**
 * Get All Routes By Driver Profile ID Query
 *
 * @param driverProfileId driver profile id
 */
public record GetAllRoutesByDriverProfileIdQuery(Long driverProfileId) {
    public GetAllRoutesByDriverProfileIdQuery {
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Driver profile id cannot be null or less than 1");
        }
    }
}
