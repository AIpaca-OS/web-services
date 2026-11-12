package com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for notification persistence entities.
 */
@Repository
public interface NotificationPersistenceRepository extends JpaRepository<NotificationPersistenceEntity, Long> {
    List<NotificationPersistenceEntity> findAllByOrderByCreatedAtDesc();

    List<NotificationPersistenceEntity> findAllByStudentIdOrderByCreatedAtDesc(StudentId studentId);

    List<NotificationPersistenceEntity> findAllByRecipientProfileIdOrderByCreatedAtDesc(ProfileId recipientProfileId);

    boolean existsByStudentIdAndTripIdAndJourneyEventAndRecipientProfileId(StudentId studentId, TripId tripId,
                                                                          JourneyEvent journeyEvent,
                                                                          ProfileId recipientProfileId);
}
