package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get Profile By Account ID Query
 *
 * @param accountId account id
 */
public record GetProfileByAccountIdQuery(Long accountId) {
    /**
     * Compact constructor for GetProfileByAccountIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetProfileByAccountIdQuery {
        if (accountId == null || accountId < 1) {
            throw new IllegalArgumentException("Account id cannot be null or less than 1");
        }
    }
}
