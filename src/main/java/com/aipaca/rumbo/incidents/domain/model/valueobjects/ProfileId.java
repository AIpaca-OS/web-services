package com.aipaca.rumbo.incidents.domain.model.valueobjects;

/**
 * Value object representing the profile id.
 *
 * <p>Links an incident, a delay or an acknowledgement to a profile (driver or tutor) of the Profiles bounded context.</p>
 *
 * @param profileId The profile id. It cannot be null or less than 1.
 */
public record ProfileId(Long profileId) {
    /**
     * Compact constructor for ProfileId.
     *
     * @throws IllegalArgumentException if the profile id is null or less than 1.
     */
    public ProfileId {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }
}
