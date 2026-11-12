package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get Profile By ID Query
 *
 * @param profileId profile id
 */
public record GetProfileByIdQuery(Long profileId) {
    /**
     * Compact constructor for GetProfileByIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetProfileByIdQuery {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }
}
