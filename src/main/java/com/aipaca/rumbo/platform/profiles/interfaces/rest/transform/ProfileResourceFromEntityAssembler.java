package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ProfileResource;

/**
 * Assembler to convert a Profile entity to a ProfileResource.
 */
public class ProfileResourceFromEntityAssembler {
    /**
     * Converts a Profile entity to a ProfileResource.
     *
     * @param entity The {@link Profile} entity to convert.
     * @return The {@link ProfileResource} resource.
     */
    public static ProfileResource toResourceFromEntity(Profile entity) {
        return new ProfileResource(
                entity.getId(),
                entity.getAccountId().accountId(),
                entity.getProfileType(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getPhoneNumber());
    }
}
