package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get Active Tutor Student Relationship By Tutor ID And Student ID Query
 *
 * @param tutorId   tutor profile id
 * @param studentId student id
 */
public record GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery(Long tutorId, Long studentId) {
    /**
     * Compact constructor for GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery {
        if (tutorId == null || tutorId < 1) {
            throw new IllegalArgumentException("Tutor id cannot be null or less than 1");
        }
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
