package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get All Tutor Student Relationships By Student ID Query
 *
 * @param studentId student id
 */
public record GetAllTutorStudentRelationshipsByStudentIdQuery(Long studentId) {
    /**
     * Compact constructor for GetAllTutorStudentRelationshipsByStudentIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllTutorStudentRelationshipsByStudentIdQuery {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
