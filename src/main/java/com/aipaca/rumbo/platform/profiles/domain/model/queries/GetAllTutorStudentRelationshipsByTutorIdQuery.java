package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get All Tutor Student Relationships By Tutor ID Query
 *
 * @param tutorId tutor profile id
 */
public record GetAllTutorStudentRelationshipsByTutorIdQuery(Long tutorId) {
    /**
     * Compact constructor for GetAllTutorStudentRelationshipsByTutorIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllTutorStudentRelationshipsByTutorIdQuery {
        if (tutorId == null || tutorId < 1) {
            throw new IllegalArgumentException("Tutor id cannot be null or less than 1");
        }
    }
}
