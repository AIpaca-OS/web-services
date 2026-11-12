package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateProfileContactCommand;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.UpdateProfileContactResource;

/**
 * Assembler to convert an UpdateProfileContactResource to an UpdateProfileContactCommand.
 */
public class UpdateProfileContactCommandFromResourceAssembler {
    /**
     * Converts an UpdateProfileContactResource to an UpdateProfileContactCommand.
     *
     * @param profileId The profile id taken from the path.
     * @param resource The {@link UpdateProfileContactResource} resource to convert.
     * @return The {@link UpdateProfileContactCommand} command.
     */
    public static UpdateProfileContactCommand toCommandFromResource(Long profileId, UpdateProfileContactResource resource) {
        return new UpdateProfileContactCommand(profileId, resource.phoneNumber());
    }
}
