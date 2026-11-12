package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get School By ID Query
 *
 * @param schoolId school id
 */
public record GetSchoolByIdQuery(Long schoolId) {
    /**
     * Compact constructor for GetSchoolByIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetSchoolByIdQuery {
        if (schoolId == null || schoolId < 1) {
            throw new IllegalArgumentException("School id cannot be null or less than 1");
        }
    }
}
