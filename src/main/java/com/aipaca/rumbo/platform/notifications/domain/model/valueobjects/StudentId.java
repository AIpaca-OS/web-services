package com.aipaca.rumbo.platform.notifications.domain.model.valueobjects;

/**
 * Value object representing the student id.
 *
 * <p>Links a notification to a student of the Profiles bounded context.</p>
 *
 * @param studentId The student id. It cannot be null or less than 1.
 */
public record StudentId(Long studentId) {
    public StudentId {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
