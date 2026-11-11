package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetTutorStudentRelationshipByTutorIdAndStudentIdQuery(
        Long tutorId,
        Long studentId
) {
    public GetTutorStudentRelationshipByTutorIdAndStudentIdQuery {
        if (tutorId == null || tutorId < 1) {
            throw new IllegalArgumentException(
                    "Tutor id cannot be null or less than 1"
            );
        }

        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException(
                    "Student id cannot be null or less than 1"
            );
        }
    }
}
