package com.aipaca.rumbo.platform.notifications.application.internal.eventhandlers;

import com.aipaca.rumbo.platform.notifications.application.commandservices.NotificationCommandService;
import com.aipaca.rumbo.platform.notifications.application.internal.outboundservices.acl.TripAudienceAcl;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateStudentJourneyNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.platform.shared.application.events.StudentJourneyEvent;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Stores notification records in the same PostgreSQL transaction as the business fact.
 * If persistence fails, the originating command is rolled back instead of silently
 * leaving a trip completed without its corresponding notices.
 */
@Component
public class StudentJourneyNotificationHandler {
    private final NotificationCommandService commands;
    private final TripAudienceAcl trips;

    public StudentJourneyNotificationHandler(NotificationCommandService commands, TripAudienceAcl trips) {
        this.commands = commands;
        this.trips = trips;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(StudentJourneyEvent event) {
        Long routeId = event.routeId();
        List<Long> studentIds = event.studentIds();
        if (event.tripId() != null && (routeId == null || studentIds.isEmpty())) {
            var audience = trips.of(event.tripId());
            routeId = audience.routeId();
            studentIds = audience.studentIds();
        }
        if (routeId == null || routeId < 1)
            throw new IllegalStateException("Notification event has no route id");

        for (Long studentId : studentIds.stream().distinct().toList()) {
            Result<Long, ApplicationError> result;
            if ("INCIDENT".equals(event.kind()) || "DELAY".equals(event.kind())) {
                result = commands.handle(new CreateNotificationCommand(
                        NotificationType.WARNING,
                        "INCIDENT".equals(event.kind()) ? NotificationPriority.CRITICAL : NotificationPriority.REGULAR,
                        event.title(), event.message(), routeId, studentId, null));
            } else {
                result = commands.handle(new CreateStudentJourneyNotificationCommand(
                        studentId, routeId, event.tripId(), JourneyEvent.valueOf(event.kind()),
                        event.title(), event.message(), null));
            }
            if (result instanceof Result.Failure<Long, ApplicationError> failure) {
                // Replaying a unique trip transition is safe: the notification already exists.
                if ("NOTIFICATION_CONFLICT".equals(failure.error().code())) continue;
                throw new IllegalStateException("Cannot persist journey notification: " + failure.error().message()
                        + "; " + failure.error().details());
            }
        }
    }
}
