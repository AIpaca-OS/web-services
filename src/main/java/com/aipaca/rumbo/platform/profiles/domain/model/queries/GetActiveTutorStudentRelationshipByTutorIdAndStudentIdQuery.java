package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/** Gets the current authorized relationship; revoked history may contain older records for the same pair. */
public record GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery(
        Long tutorId,
        Long studentId
) {
    public GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery {
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