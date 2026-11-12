package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get All Service Enrollments By Driver ID Query
 *
 * @param driverId driver profile id
 */
public record GetAllServiceEnrollmentsByDriverIdQuery(Long driverId) {
    /**
     * Compact constructor for GetAllServiceEnrollmentsByDriverIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllServiceEnrollmentsByDriverIdQuery {
        if (driverId == null || driverId < 1) {
            throw new IllegalArgumentException("Driver id cannot be null or less than 1");
        }
    }
}
