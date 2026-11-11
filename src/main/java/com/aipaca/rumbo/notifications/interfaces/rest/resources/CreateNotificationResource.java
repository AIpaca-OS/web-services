package com.aipaca.rumbo.notifications.interfaces.rest.resources;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Schema(
        name = "CreateNotificationRequest",
        description = "Request payload for creating a new notification",
        example = "{\"type\": \"INFO\", \"priority\": \"REGULAR\", \"title\": \"Route update\", \"message\": \"Your child is on the way\", \"routeId\": 1, \"studentId\": 1}"
)
public record CreateNotificationResource(

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Notification type", example = "INFO")
        NotificationType type,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Notification priority", example = "REGULAR")
        NotificationPriority priority,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Notification title", example = "Route update", minLength = 1, maxLength = 100)
        String title,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Notification message", example = "Your child is on the way", minLength = 1, maxLength = 500)
        String message,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Associated route identifier", example = "1")
        Long routeId,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Associated student identifier", example = "1")
        Long studentId
) {
}