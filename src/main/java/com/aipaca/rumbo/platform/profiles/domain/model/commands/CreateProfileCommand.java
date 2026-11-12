package com.aipaca.rumbo.platform.profiles.domain.model.commands;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;

/**
 * Create Profile Command
 *
 * @param accountId   account that owns the profile
 * @param profileType profile type
 * @param firstName   first name
 * @param lastName    last name
 * @param phoneNumber contact phone number
 */
public record CreateProfileCommand(
        AccountId accountId,
        ProfileType profileType,
        String firstName,
        String lastName,
        String phoneNumber
) {
    /**
     * Compact constructor for CreateProfileCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public CreateProfileCommand {
        if (accountId == null) {
            throw new IllegalArgumentException("Account id is required");
        }
        if (profileType == null) {
            throw new IllegalArgumentException("Profile type is required");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
    }
}
