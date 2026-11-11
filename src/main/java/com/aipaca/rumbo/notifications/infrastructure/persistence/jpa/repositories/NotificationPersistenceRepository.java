package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationPersistenceRepository extends JpaRepository<NotificationPersistenceEntity, Long> {
}
