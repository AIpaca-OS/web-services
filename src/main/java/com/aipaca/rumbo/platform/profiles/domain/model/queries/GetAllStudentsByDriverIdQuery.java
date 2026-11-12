package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get All Students By Driver ID Query
 *
 * @param driverId driver profile id
 */
public record GetAllStudentsByDriverIdQuery(Long driverId) {
    /**
     * Compact constructor for GetAllStudentsByDriverIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllStudentsByDriverIdQuery {
        if (driverId == null || driverId < 1) {
            throw new IllegalArgumentException("Driver id cannot be null or less than 1");
        }
    }
}
