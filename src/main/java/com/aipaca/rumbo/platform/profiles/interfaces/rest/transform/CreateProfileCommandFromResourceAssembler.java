package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.CreateProfileResource;

/**
 * Assembler to convert a CreateProfileResource to a CreateProfileCommand.
 */
public class CreateProfileCommandFromResourceAssembler {
    /**
     * Converts a CreateProfileResource to a CreateProfileCommand.
     *
     * @param resource The {@link CreateProfileResource} resource to convert.
     * @return The {@link CreateProfileCommand} command.
     */
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                new AccountId(resource.accountId()),
                resource.profileType(),
                resource.firstName(),
                resource.lastName(),
                resource.phoneNumber());
    }
}
