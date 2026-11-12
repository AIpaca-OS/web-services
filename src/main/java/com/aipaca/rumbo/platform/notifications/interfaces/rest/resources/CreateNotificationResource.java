package com.aipaca.rumbo.platform.notifications.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a notification.
 */
@Schema(
    name = "CreateNotificationRequest",
    description = "Request payload for creating a notification",
    example = "{\"type\": \"INFO\", \"priority\": \"REGULAR\", \"title\": \"Ruta publicada\", \"message\": \"La ruta ya está disponible.\", \"routeId\": 1, \"studentId\": null, \"recipientProfileId\": null}"
)
public record CreateNotificationResource(
    @NotBlank
    @Schema(description = "Notification type: WARNING, SUCCESS, IMPORTANT or INFO", example = "INFO")
    String type,

    @NotBlank
    @Schema(description = "Notification priority: CRITICAL or REGULAR", example = "REGULAR")
    String priority,

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Notification title", example = "Ruta publicada", maxLength = 120)
    String title,

    @NotBlank
    @Size(max = 2000)
    @Schema(description = "Notification message", example = "La ruta ya está disponible.", maxLength = 2000)
    String message,

    @Positive
    @Schema(description = "Related route id", example = "1", nullable = true)
    Long routeId,

    @Positive
    @Schema(description = "Related student id", example = "1", nullable = true)
    Long studentId,

    @Positive
    @Schema(description = "Recipient profile id; omit it for a notice to every user", example = "1", nullable = true)
    Long recipientProfileId
) {
}
