package com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.notifications.domain.repositories.NotificationRepository;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.assemblers.NotificationPersistenceAssembler;
import com.aipaca.rumbo.platform.notifications.infrastructure.persistence.jpa.repositories.NotificationPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain notification repository port with Spring Data JPA.
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationPersistenceRepository notificationPersistenceRepository;

    public NotificationRepositoryImpl(NotificationPersistenceRepository notificationPersistenceRepository) {
        this.notificationPersistenceRepository = notificationPersistenceRepository;
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationPersistenceRepository.findById(id).map(NotificationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Notification> findAll() {
        return notificationPersistenceRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Notification> findAllByStudentId(StudentId studentId) {
        return notificationPersistenceRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(NotificationPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Notification> findAllByRecipientProfileId(ProfileId recipientProfileId) {
        return notificationPersistenceRepository.findAllByRecipientProfileIdOrderByCreatedAtDesc(recipientProfileId)
                .stream().map(NotificationPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Notification save(Notification notification) {
        var savedEntity = notificationPersistenceRepository.save(
                NotificationPersistenceAssembler.toPersistenceFromDomain(notification));
        return NotificationPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public boolean existsById(Long id) {
        return notificationPersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByStudentIdAndTripIdAndJourneyEventAndRecipientProfileId(StudentId studentId, TripId tripId,
                                                                             JourneyEvent journeyEvent,
                                                                             ProfileId recipientProfileId) {
        return notificationPersistenceRepository.existsByStudentIdAndTripIdAndJourneyEventAndRecipientProfileId(
                studentId, tripId, journeyEvent, recipientProfileId);
    }

    @Override
    public void deleteById(Long id) {
        notificationPersistenceRepository.deleteById(id);
    }
}
