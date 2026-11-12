package com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.notifications.domain.repositories.NotificationRepository;
import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import com.aipaca.rumbo.notifications.infrastructure.persistence.jpa.repositories.NotificationJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {
    private final NotificationJpaRepository jpa;
    public NotificationRepositoryAdapter(NotificationJpaRepository jpa) { this.jpa=jpa; }
    private Notification domain(NotificationPersistenceEntity e) {
        return new Notification(e.getId(), e.getType(), e.getPriority(),
                e.getTitle(), e.getMessage(), e.getCreatedAt(), e.getReadAt(),
                e.getRouteId(), e.getStudentId());
    }
    private NotificationPersistenceEntity persistence(Notification n) {
        var e=new NotificationPersistenceEntity();
        e.setId(n.getId());e.setType(n.getType());e.setPriority(n.getPriority());
        e.setTitle(n.getTitle());e.setMessage(n.getMessage());
        e.setCreatedAt(n.getCreatedAt());e.setReadAt(n.getReadAt());
        e.setRouteId(n.getRouteId());e.setStudentId(n.getStudentId());
        return e;
    }
    @Override public List<Notification> findAll() { return jpa.findAll().stream().map(this::domain).toList(); }
    @Override public Optional<Notification> findById(Long id) { return jpa.findById(id).map(this::domain); }
    @Override public Notification save(Notification n) { return domain(jpa.save(persistence(n))); }
    @Override public void delete(Long id) { jpa.deleteById(id); }
}
