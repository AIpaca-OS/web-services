package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.DataDeletionRequestResource;
public final class DataDeletionRequestResourceFromEntityAssembler {
    private DataDeletionRequestResourceFromEntityAssembler() { }
    public static DataDeletionRequestResource toResourceFromEntity(DataDeletionRequest r) {
        return new DataDeletionRequestResource(r.getId(),r.getStudentId(),r.getRequestedByProfileId(),r.getStatus(),r.getRequestedAt(),r.getResolvedAt(),r.getResolutionNote());
    }
}
