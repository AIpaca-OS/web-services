package com.aipaca.rumbo.platform.profiles.domain.model.commands;

public record UpdateProfileContactCommand(
        Long profileId,
        String phoneNumber
) {
    public UpdateProfileContactCommand {
        if (profileId == null || profileId < 1)
            throw new IllegalArgumentException("Invalid profile id");

        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Phone number is required");
    }
}
