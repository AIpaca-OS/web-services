package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;

import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.TutorStudentRelationshipResource;

/**
 * Assembler to convert a TutorStudentRelationship entity to a TutorStudentRelationshipResource.
 */
public class TutorStudentRelationshipResourceFromEntityAssembler {
    /**
     * Converts a TutorStudentRelationship entity to a TutorStudentRelationshipResource.
     *
     * @param entity The {@link TutorStudentRelationship} entity to convert.
     * @return The {@link TutorStudentRelationshipResource} resource.
     */
    public static TutorStudentRelationshipResource toResourceFromEntity(TutorStudentRelationship entity) {
        return new TutorStudentRelationshipResource(
                entity.getId(),
                entity.getTutorProfileId(),
                entity.getAuthorizedByProfileId(),
                entity.getRelationshipType(),
                entity.getAuthorizationStatus(),
                entity.getAuthorizedAt(),
                entity.getRevokedAt());
    }
}
