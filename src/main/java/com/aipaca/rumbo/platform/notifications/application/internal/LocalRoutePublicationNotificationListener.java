package com.aipaca.rumbo.platform.notifications.application.internal;

import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.platform.routes.domain.events.RoutePublishedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Local-only demo event adapter. Never delivers to all real tutors.
 * An authenticated subscriber/guardian audience must replace this adapter.
 */
@Component
@ConditionalOnProperty(prefix="app.demo", name="enabled", havingValue="true")
public class LocalRoutePublicationNotificationListener {
    private final NotificationService notifications;

    public LocalRoutePublicationNotificationListener(NotificationService notifications) {
        this.notifications = notifications;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoutePublished(RoutePublishedEvent event) {
        notifications.create(new CreateNotificationCommand(
                NotificationType.info, NotificationPriority.regular,
                "Ruta publicada (demostración)",
                "Se publicó una ruta. Consulta tus traslados autorizados cuando IAM y Profiles estén conectados.",
                event.routeId(), null));
    }
}
