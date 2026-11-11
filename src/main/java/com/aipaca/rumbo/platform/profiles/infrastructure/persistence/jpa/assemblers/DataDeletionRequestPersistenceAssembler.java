package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.DataDeletionRequestPersistenceEntity;

/** Translates stored request state without automatically approving or anonymizing a student. */
public final class DataDeletionRequestPersistenceAssembler {
    private DataDeletionRequestPersistenceAssembler() { }

    public static DataDeletionRequest toDomainFromPersistence(DataDeletionRequestPersistenceEntity entity) {
        if (entity == null) return null;
        return DataDeletionRequest.reconstitute(entity.getId(), entity.getStudentId(),
                entity.getRequestedByProfileId(), entity.getStatus(), entity.getRequestedAt(),
                entity.getResolvedAt(), entity.getResolutionNote());
    }

    public static DataDeletionRequestPersistenceEntity toPersistenceFromDomain(DataDeletionRequest request) {
        var entity = new DataDeletionRequestPersistenceEntity();
        entity.setId(request.getId());
        entity.setStudentId(request.getStudentId());
        entity.setRequestedByProfileId(request.getRequestedByProfileId());
        entity.setStatus(request.getStatus());
        entity.setRequestedAt(request.getRequestedAt());
        entity.setResolvedAt(request.getResolvedAt());
        entity.setResolutionNote(request.getResolutionNote());
        return entity;
    }
}
