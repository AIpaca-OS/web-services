package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get Student By ID Query
 *
 * @param studentId student id
 */
public record GetStudentByIdQuery(Long studentId) {
    /**
     * Compact constructor for GetStudentByIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetStudentByIdQuery {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
