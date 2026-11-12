package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.SchoolResource;

/**
 * Assembler to convert a School entity to a SchoolResource.
 */
public class SchoolResourceFromEntityAssembler {
    /**
     * Converts a School entity to a SchoolResource.
     *
     * @param entity The {@link School} entity to convert.
     * @return The {@link SchoolResource} resource.
     */
    public static SchoolResource toResourceFromEntity(School entity) {
        return new SchoolResource(entity.getId(), entity.getName(), entity.getAddress(), entity.getDistrict());
    }
}
