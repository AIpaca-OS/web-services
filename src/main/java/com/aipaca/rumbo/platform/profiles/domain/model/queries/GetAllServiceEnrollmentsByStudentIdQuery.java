package com.aipaca.rumbo.platform.profiles.domain.model.queries;

public record GetAllServiceEnrollmentsByStudentIdQuery(
        Long studentId
) {
    public GetAllServiceEnrollmentsByStudentIdQuery {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException(
                    "Student id cannot be null or less than 1"
            );
        }
    }
}
