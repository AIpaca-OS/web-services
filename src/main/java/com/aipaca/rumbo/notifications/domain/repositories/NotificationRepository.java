package com.aipaca.rumbo.notifications.domain.repositories;

import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;

import java.util.List;

public interface NotificationRepository {

    List<Notification> findAll();
    Notification save(Notification notification);
    void delete(Long id);

}
