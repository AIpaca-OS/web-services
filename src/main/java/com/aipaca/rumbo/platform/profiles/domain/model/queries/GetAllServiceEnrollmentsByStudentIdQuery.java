package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get All Service Enrollments By Student ID Query
 *
 * @param studentId student id
 */
public record GetAllServiceEnrollmentsByStudentIdQuery(Long studentId) {
    /**
     * Compact constructor for GetAllServiceEnrollmentsByStudentIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllServiceEnrollmentsByStudentIdQuery {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
