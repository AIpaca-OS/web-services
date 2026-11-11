package com.aipaca.rumbo.notifications.domain.model.agreggates;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import jdk.internal.joptsimple.internal.Strings;
import java.time.Instant;

public class Notification extends AbstractDomainAggregateRoot<Notification>{

    private Long id;

    private NotificationType type;

    private NotificationPriority priority;

    private String title;

    private String message;

    private String readAt;

    private Boolean read;

    private RouteId routeId;

    private StudentId studentId;


    public Notification(){
        this.type=NotificationType.info;
        this.priority=NotificationPriority.regular;
        this.title = Strings.EMPTY;
        this.message = Strings.EMPTY;
        this.readAt = Strings.EMPTY;
        this.read = false;
    }

    public Notification(CreateNotificationCommand command) {
        this.type = command.type();
        this.priority = command.priority();
        this.title = command.title();
        this.message = command.message();
        this.readAt = command.readAt();
        this.read = command.read();
        this.routeId = command.routeId();
        this.studentId = command.studentId();
    }

    public static Notification reconstitute(
            Long id,
            NotificationType type,
            NotificationPriority priority,
            String title,
            String message,
            String readAt,
            Boolean read,
            RouteId routeId,
            StudentId studentId
    ) {
        return new Notification(id, type, priority, title, message, readAt, read, routeId, studentId);
    }

    public void maskAsRead()
    {
        this.read=true;
        this.readAt= Instant.now().toString();
    }









}
