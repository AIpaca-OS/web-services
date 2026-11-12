package com.aipaca.rumbo.platform.notifications.interfaces.rest.resources;

import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationCategory;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resource for a notification.
 */
@Schema(name = "NotificationResponse", description = "Notification information response")
public record NotificationResource(
    @Schema(description = "Notification unique identifier", example = "1")
    Long id,

    @Schema(description = "Notification category", example = "GENERAL")
    NotificationCategory category,

    @Schema(description = "Notification type", example = "INFO")
    NotificationType type,

    @Schema(description = "Notification priority", example = "REGULAR")
    NotificationPriority priority,

    @Schema(description = "Notification title", example = "Ruta publicada")
    String title,

    @Schema(description = "Notification message", example = "La ruta ya está disponible.")
    String message,

    @Schema(description = "Creation date")
    Instant createdAt,

    @Schema(description = "Read date", nullable = true)
    Instant readAt,

    @Schema(description = "Whether the notification was read", example = "false")
    boolean read,

    @Schema(description = "Related route id", example = "1", nullable = true)
    Long routeId,

    @Schema(description = "Related trip id", example = "1", nullable = true)
    Long tripId,

    @Schema(description = "Related student id", nullable = true)
    Long studentId,

    @Schema(description = "Recipient profile id; null when addressed to every user", example = "1", nullable = true)
    Long recipientProfileId,

    @Schema(description = "Journey fact of a STUDENT_JOURNEY notification", nullable = true)
    JourneyEvent journeyEvent
) {
}
