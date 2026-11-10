package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.TutorStudentRelationshipPersistenceEntity;

/** Translates relationship state without granting authorization or changing its dates. */
public final class TutorStudentRelationshipPersistenceAssembler {
    private TutorStudentRelationshipPersistenceAssembler() {
    }

    public static TutorStudentRelationship toDomainFromPersistence(TutorStudentRelationshipPersistenceEntity entity) {
        if (entity == null) return null;
        return TutorStudentRelationship.reconstitute(
                entity.getId(), entity.getTutorProfileId(), entity.getStudentId(), entity.getAuthorizedByProfileId(),
                entity.getRelationshipType(), entity.getAuthorizationStatus(), entity.getAuthorizedAt(), entity.getRevokedAt());
    }

    public static TutorStudentRelationshipPersistenceEntity toPersistenceFromDomain(TutorStudentRelationship relationship) {
        var entity = new TutorStudentRelationshipPersistenceEntity();
        entity.setId(relationship.getId());
        entity.setTutorProfileId(relationship.getTutorProfileId());
        entity.setStudentId(relationship.getStudentId());
        entity.setAuthorizedByProfileId(relationship.getAuthorizedByProfileId());
        entity.setRelationshipType(relationship.getRelationshipType());
        entity.setAuthorizationStatus(relationship.getAuthorizationStatus());
        entity.setAuthorizedAt(relationship.getAuthorizedAt());
        entity.setRevokedAt(relationship.getRevokedAt());
        return entity;
    }
}
