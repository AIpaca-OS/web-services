package com.aipaca.rumbo.platform.profiles.domain.model.valueobjects;

/**
 * Value object representing the account id.
 *
 * <p>
 * This value object links a profile to the account that owns it. Accounts belong to the
 * IAM bounded context, which is not implemented yet, so only the identity is kept.
 * It must be a positive Long value.
 * </p>
 *
 * @param accountId The account id. It cannot be null or less than 1.
 */
public record AccountId(Long accountId) {
    /**
     * Compact constructor for AccountId.
     * Validates that the accountId is not null and is greater than or equal to 1.
     *
     * @throws IllegalArgumentException if the accountId is null or less than 1.
     */
    public AccountId {
        if (accountId == null || accountId < 1) {
            throw new IllegalArgumentException("Account id cannot be null or less than 1");
        }
    }
}
