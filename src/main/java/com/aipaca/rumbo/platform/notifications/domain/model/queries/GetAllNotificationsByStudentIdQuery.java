package com.aipaca.rumbo.platform.notifications.domain.model.queries;

/**
 * Get All Notifications By Student ID Query
 *
 * @param studentId student id
 */
public record GetAllNotificationsByStudentIdQuery(Long studentId) {
    public GetAllNotificationsByStudentIdQuery {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
    }
}
