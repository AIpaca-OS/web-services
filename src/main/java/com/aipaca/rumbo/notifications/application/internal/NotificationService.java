package com.aipaca.rumbo.notifications.application.internal;

import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.notifications.domain.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository repository;
    public NotificationService(NotificationRepository repository) { this.repository=repository; }
    @Transactional(readOnly = true)
    public List<Notification> getAll() { return repository.findAll(); }
    @Transactional(readOnly = true)
    public Notification get(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
    }
    public Notification create(CreateNotificationCommand command) {
        return repository.save(new Notification(command));
    }
    public Notification markRead(Long id) {
        Notification n=get(id); n.markAsRead(); return repository.save(n);
    }
    public void delete(Long id) {
        get(id);
        repository.delete(id);
    }
}
