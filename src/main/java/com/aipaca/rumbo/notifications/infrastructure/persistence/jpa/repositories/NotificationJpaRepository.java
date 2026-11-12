package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationPersistenceEntity, Long> {}
