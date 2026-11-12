package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.TutorStudentRelationshipResource;
public final class TutorStudentRelationshipResourceFromEntityAssembler {
    private TutorStudentRelationshipResourceFromEntityAssembler() { }
    public static TutorStudentRelationshipResource toResourceFromEntity(TutorStudentRelationship r) {
        return new TutorStudentRelationshipResource(r.getId(),r.getTutorProfileId(),r.getAuthorizedByProfileId(),r.getRelationshipType(),r.getAuthorizationStatus(),r.getAuthorizedAt(),r.getRevokedAt());
    }
}
