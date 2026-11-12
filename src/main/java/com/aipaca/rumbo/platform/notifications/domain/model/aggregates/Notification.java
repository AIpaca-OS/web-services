package com.aipaca.rumbo.platform.notifications.domain.model.aggregates;

import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateStudentJourneyNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.*;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * Notification aggregate root.
 *
 * <p>
 * A notification belongs to a {@link NotificationCategory}: a general notice or a fact of the journey
 * of one student addressed to its tutors. Delays and incidents are business facts of the Incident &amp;
 * Delay Management context, not notifications. Routes, trips, students and profiles belong to other
 * bounded contexts, so a notification keeps only their identities through {@link RouteId},
 * {@link TripId}, {@link StudentId} and {@link ProfileId}; a null recipient addresses every user. The creation date is assigned by the
 * persistence auditing and restored on reconstitution.
 * </p>
 */
@Getter
public class Notification extends AbstractDomainAggregateRoot<Notification> {
    /**
     * The unique identifier for the notification.
     */
    private Long id;

    /**
     * The category of the notification.
     */
    private NotificationCategory category;

    /**
     * The visual type of the notification.
     */
    private NotificationType type;

    /**
     * The delivery priority.
     */
    private NotificationPriority priority;

    /**
     * The notification title.
     */
    private String title;

    /**
     * The notification message.
     */
    private String message;

    /**
     * The related route, or null.
     */
    private RouteId routeId;

    /**
     * The related trip, or null.
     */
    private TripId tripId;

    /**
     * The related student, or null.
     */
    private StudentId studentId;

    /**
     * The recipient profile, or null when the notification is addressed to every user.
     */
    private ProfileId recipientProfileId;

    /**
     * The journey fact of a student journey notification, or null.
     */
    private JourneyEvent journeyEvent;

    /**
     * The creation date.
     */
    private Instant createdAt;

    /**
     * The read date, if read.
     */
    private Instant readAt;

    private Notification() {
    }

    /**
     * Constructor with a CreateNotificationCommand. Creates a general notification.
     *
     * @param command The {@link CreateNotificationCommand} instance
     */
    public Notification(CreateNotificationCommand command) {
        this.category = NotificationCategory.GENERAL;
        this.type = command.type();
        this.priority = command.priority();
        this.title = requireText(command.title(), "Title", 120);
        this.message = requireText(command.message(), "Message", 2000);
        this.routeId = command.routeId() == null ? null : new RouteId(command.routeId());
        this.studentId = command.studentId() == null ? null : new StudentId(command.studentId());
        this.recipientProfileId = command.recipientProfileId() == null
                ? null : new ProfileId(command.recipientProfileId());
    }

    /**
     * Constructor with a CreateStudentJourneyNotificationCommand. Creates a notification about the
     * journey of one student, addressed to its tutors. The visual type and the priority follow the
     * journey fact.
     *
     * @param command The {@link CreateStudentJourneyNotificationCommand} instance
     */
    public Notification(CreateStudentJourneyNotificationCommand command) {
        this.category = NotificationCategory.STUDENT_JOURNEY;
        this.studentId = new StudentId(command.studentId());
        this.routeId = new RouteId(command.routeId());
        this.tripId = command.tripId() == null ? null : new TripId(command.tripId());
        this.journeyEvent = command.journeyEvent();
        this.recipientProfileId = command.recipientProfileId() == null
                ? null : new ProfileId(command.recipientProfileId());
        this.title = requireText(command.title(), "Title", 120);
        this.message = requireText(command.message(), "Message", 2000);
        this.type = switch (command.journeyEvent()) {
            case ABSENT, TRIP_CANCELLED -> NotificationType.WARNING;
            case STUDENT_ARRIVED, TRIP_COMPLETED -> NotificationType.SUCCESS;
            default -> NotificationType.INFO;
        };
        this.priority = command.journeyEvent() == JourneyEvent.TRIP_CANCELLED
                ? NotificationPriority.CRITICAL
                : NotificationPriority.REGULAR;
    }

    /**
     * Reconstitutes an existing notification from persistence state.
     *
     * @return The reconstituted Notification instance.
     */
    public static Notification reconstitute(Long id, NotificationCategory category, NotificationType type,
                                            NotificationPriority priority, String title, String message,
                                            RouteId routeId, TripId tripId, StudentId studentId,
                                            ProfileId recipientProfileId, JourneyEvent journeyEvent,
                                            Instant createdAt, Instant readAt) {
        var notification = new Notification();
        notification.id = id;
        notification.category = category;
        notification.type = type;
        notification.priority = priority;
        notification.title = title;
        notification.message = message;
        notification.routeId = routeId;
        notification.tripId = tripId;
        notification.studentId = studentId;
        notification.recipientProfileId = recipientProfileId;
        notification.journeyEvent = journeyEvent;
        notification.createdAt = createdAt;
        notification.readAt = readAt;
        return notification;
    }

    /**
     * Records the first read date.
     */
    public void markAsRead() {
        if (readAt == null) {
            readAt = Instant.now();
        }
    }

    public boolean isRead() {
        return readAt != null;
    }

    public Long getRouteIdValue() {
        return routeId == null ? null : routeId.routeId();
    }

    public Long getTripIdValue() {
        return tripId == null ? null : tripId.tripId();
    }

    public Long getStudentIdValue() {
        return studentId == null ? null : studentId.studentId();
    }

    public Long getRecipientProfileIdValue() {
        return recipientProfileId == null ? null : recipientProfileId.profileId();
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be null or blank");
        }
        if (value.trim().length() > maxLength) {
            throw new IllegalArgumentException(field + " cannot exceed " + maxLength + " characters");
        }
        return value.trim();
    }
}
