package com.aipaca.rumbo.platform.trips.domain.model.valueobjects;

/**
 * Value object representing the school id.
 *
 * <p>Identifies the school served at the stop of a trip passenger, a school of the Profiles bounded context.</p>
 *
 * @param schoolId The school id. It cannot be null or less than 1.
 */
public record SchoolId(Long schoolId) {
    /**
     * Compact constructor for SchoolId.
     *
     * @throws IllegalArgumentException if the school id is null or less than 1.
     */
    public SchoolId {
        if (schoolId == null || schoolId < 1) {
            throw new IllegalArgumentException("School id cannot be null or less than 1");
        }
    }
}
