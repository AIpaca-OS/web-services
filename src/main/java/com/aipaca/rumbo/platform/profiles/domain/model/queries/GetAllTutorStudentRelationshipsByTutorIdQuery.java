package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetAllTutorStudentRelationshipsByTutorIdQuery(
        Long tutorId
) {
    public GetAllTutorStudentRelationshipsByTutorIdQuery {
        if (tutorId == null || tutorId < 1) {
            throw new IllegalArgumentException(
                    "Tutor id cannot be null or less than 1"
            );
        }
    }
}
