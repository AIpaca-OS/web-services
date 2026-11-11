package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.notifications.domain.repositories.NotificationRepository;
import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.assemblers.NotificationPersistenceAssembler;
import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.repositories.NotificationPersistenceRepository;

import java.util.List;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationPersistenceRepository notificationPersistenceRepository;

    public NotificationRepositoryImpl(
            NotificationPersistenceRepository notificationPersistenceRepository) {
        this.notificationPersistenceRepository = notificationPersistenceRepository;
    }

    @Override
    public List<Notification> findAll() {
        return notificationPersistenceRepository.findAll()
                .stream()
                .map(NotificationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Notification create(Notification notification) {
        var entity = NotificationPersistenceAssembler.toPersistenceFromDomain(notification);
        var savedEntity = notificationPersistenceRepository.save(entity);

        return NotificationPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        notificationPersistenceRepository.deleteById(id);
    }
}
