package com.aipaca.rumbo.platform.profiles.domain.model.queries;

/**
 * Get All Students By Tutor ID Query
 *
 * @param tutorId tutor profile id
 */
public record GetAllStudentsByTutorIdQuery(Long tutorId) {
    /**
     * Compact constructor for GetAllStudentsByTutorIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetAllStudentsByTutorIdQuery {
        if (tutorId == null || tutorId < 1) {
            throw new IllegalArgumentException("Tutor id cannot be null or less than 1");
        }
    }
}
