package com.aipaca.rumbo.platform.notifications.domain.model.commands;

import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;

/**
 * Create Student Journey Notification Command
 *
 * @param studentId    student id
 * @param routeId      route id
 * @param tripId       trip id, or null when the notification is not about a trip
 * @param journeyEvent journey fact
 * @param title        notification title
 * @param message      notification message
 * @param recipientProfileId recipient tutor profile id, or null for every tutor of the student
 */
public record CreateStudentJourneyNotificationCommand(
        Long studentId,
        Long routeId,
        Long tripId,
        JourneyEvent journeyEvent,
        String title,
        String message,
        Long recipientProfileId
) {
    /**
     * Compact constructor for CreateStudentJourneyNotificationCommand.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public CreateStudentJourneyNotificationCommand {
        if (studentId == null || studentId < 1) {
            throw new IllegalArgumentException("Student id cannot be null or less than 1");
        }
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
        if (tripId != null && tripId < 1) {
            throw new IllegalArgumentException("Trip id cannot be less than 1");
        }
        if (recipientProfileId != null && recipientProfileId < 1) {
            throw new IllegalArgumentException("Recipient profile id cannot be less than 1");
        }
        if (journeyEvent == null) {
            throw new IllegalArgumentException("Journey event cannot be null");
        }
    }
}
