package com.aipaca.rumbo.platform.notifications.application.internal.commandservices;

import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateStudentJourneyNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.notifications.domain.repositories.NotificationRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationCommandServiceImplTests {
    private final NotificationRepository notifications = mock(NotificationRepository.class);
    private final NotificationCommandServiceImpl service = new NotificationCommandServiceImpl(notifications);

    private static String errorCode(Result<Long, ApplicationError> result) {
        var failure = assertInstanceOf(Result.Failure.class, result);
        return ((ApplicationError) failure.error()).code();
    }

    private static CreateStudentJourneyNotificationCommand arrived() {
        return new CreateStudentJourneyNotificationCommand(
                5L, 2L, 7L, JourneyEvent.STUDENT_ARRIVED, "Llegada confirmada", "El estudiante llegó.", null);
    }

    @Test
    void sameJourneyFactForTheSameStudentAndTripIsAConflict() {
        when(notifications.existsByStudentIdAndTripIdAndJourneyEventAndRecipientProfileId(
                new StudentId(5L), new TripId(7L), JourneyEvent.STUDENT_ARRIVED, null)).thenReturn(true);
        assertEquals("NOTIFICATION_CONFLICT", errorCode(service.handle(arrived())));
        verify(notifications, never()).save(any());
    }

    @Test
    void newJourneyFactReturnsTheGeneratedId() {
        when(notifications.save(any())).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            return Notification.reconstitute(9L, notification.getCategory(), notification.getType(),
                    notification.getPriority(), notification.getTitle(), notification.getMessage(),
                    notification.getRouteId(), notification.getTripId(), notification.getStudentId(),
                    notification.getRecipientProfileId(), notification.getJourneyEvent(), null, null);
        });
        assertEquals(9L, assertInstanceOf(Result.Success.class, service.handle(arrived())).value());
    }

    @Test
    void markingAMissingNotificationIsNotFound() {
        when(notifications.findById(4L)).thenReturn(Optional.empty());
        assertEquals("NOTIFICATION_NOT_FOUND", errorCode(service.handle(new MarkNotificationAsReadCommand(4L))));
    }
}
