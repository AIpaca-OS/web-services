package com.aipaca.rumbo.notifications.interfaces.rest.resources;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for a notification.
 */
@Schema(
        name = "NotificationResponse",
        description = "Notification information response",
        example = "{\"id\": 1, \"type\": \"INFO\", \"priority\": \"REGULAR\", \"title\": \"Route update\", \"message\": \"Your child is on the way\", \"readAt\": null, \"read\": false, \"routeId\": 1, \"studentId\": 1}"
)
public record NotificationResource(
        @Schema(description = "Notification unique identifier", example = "1")
        Long id,

        @Schema(description = "Notification type", example = "INFO")
        NotificationType type,

        @Schema(description = "Notification priority", example = "REGULAR")
        NotificationPriority priority,

        @Schema(description = "Notification title", example = "Route update")
        String title,

        @Schema(description = "Notification message", example = "Your child is on the way")
        String message,

        @Schema(description = "Date and time when the notification was read", example = "2026-11-10T15:30:00")
        String readAt,

        @Schema(description = "Indicates whether the notification has been read", example = "false")
        Boolean read,

        @Schema(description = "Associated route identifier", example = "1")
        Long routeId,

        @Schema(description = "Associated student identifier", example = "1")
        Long studentId
) {
}