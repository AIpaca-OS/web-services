package com.aipaca.rumbo.platform.profiles.domain.model.commands;

/**
 * Update Profile Contact Command
 *
 * @param profileId   profile id
 * @param phoneNumber contact phone number
 */
public record UpdateProfileContactCommand(Long profileId, String phoneNumber) {
    /**
     * Compact constructor for UpdateProfileContactCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public UpdateProfileContactCommand {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Invalid profile id");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
    }
}
