package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetAllStudentsByTutorIdQuery(Long tutorId) {
    public GetAllStudentsByTutorIdQuery {
        if (tutorId == null || tutorId < 1) {
            throw new IllegalArgumentException(
                    "Tutor id cannot be null or less than 1"
            );
        }
    }
}
