package com.aipaca.rumbo.platform.notifications.domain.repositories;

import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.TripId;

import java.util.List;
import java.util.Optional;

/**
 * Notification repository port.
 */
public interface NotificationRepository {
    Optional<Notification> findById(Long id);

    List<Notification> findAll();

    List<Notification> findAllByStudentId(StudentId studentId);

    List<Notification> findAllByRecipientProfileId(ProfileId recipientProfileId);

    Notification save(Notification notification);

    boolean existsById(Long id);

    boolean existsByStudentIdAndTripIdAndJourneyEventAndRecipientProfileId(StudentId studentId, TripId tripId,
                                                                      JourneyEvent journeyEvent,
                                                                      ProfileId recipientProfileId);

    void deleteById(Long id);
}
