package com.aipaca.rumbo.platform.notifications.domain.repositories;

import com.aipaca.rumbo.platform.notifications.domain.model.agreggates.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findAll();
    Optional<Notification> findById(Long id);
    Notification save(Notification notification);
    void delete(Long id);
}
