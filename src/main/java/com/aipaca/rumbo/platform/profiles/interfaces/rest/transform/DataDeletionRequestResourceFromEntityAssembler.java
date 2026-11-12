package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.DataDeletionRequestResource;

/**
 * Assembler to convert a DataDeletionRequest entity to a DataDeletionRequestResource.
 */
public class DataDeletionRequestResourceFromEntityAssembler {
    /**
     * Converts a DataDeletionRequest entity to a DataDeletionRequestResource.
     *
     * @param entity The {@link DataDeletionRequest} entity to convert.
     * @return The {@link DataDeletionRequestResource} resource.
     */
    public static DataDeletionRequestResource toResourceFromEntity(DataDeletionRequest entity) {
        return new DataDeletionRequestResource(
                entity.getId(),
                entity.getStudentId(),
                entity.getRequestedByProfileId(),
                entity.getStatus(),
                entity.getRequestedAt(),
                entity.getResolvedAt(),
                entity.getResolutionNote());
    }
}
