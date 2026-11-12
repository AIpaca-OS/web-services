package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get Service Enrollment By ID Query
 *
 * @param enrollmentId enrollment id
 */
public record GetServiceEnrollmentByIdQuery(Long enrollmentId) {
    /**
     * Compact constructor for GetServiceEnrollmentByIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetServiceEnrollmentByIdQuery {
        if (enrollmentId == null || enrollmentId < 1) {
            throw new IllegalArgumentException("Enrollment id cannot be null or less than 1");
        }
    }
}
