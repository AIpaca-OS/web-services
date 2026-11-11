package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetTutorStudentRelationshipByIdQuery(
        Long relationshipId
) {
    public GetTutorStudentRelationshipByIdQuery {
        if (relationshipId == null || relationshipId < 1) {
            throw new IllegalArgumentException(
                    "Relationship id cannot be null or less than 1"
            );
        }
    }
}
