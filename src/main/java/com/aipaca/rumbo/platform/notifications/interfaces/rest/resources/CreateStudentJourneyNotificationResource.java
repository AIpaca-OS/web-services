package com.aipaca.rumbo.platform.notifications.interfaces.rest.resources;

import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.JourneyEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a student journey notification.
 */
@Schema(
    name = "CreateStudentJourneyNotificationRequest",
    description = "Request payload for notifying the tutors of a student about its journey",
    example = "{\"studentId\": 1, \"routeId\": 1, \"tripId\": 1, \"journeyEvent\": \"TRIP_STARTED\", \"title\": \"Viaje iniciado\", \"message\": \"El conductor marcó el inicio del recorrido.\", \"recipientProfileId\": 1}"
)
public record CreateStudentJourneyNotificationResource(
    @NotNull
    @Positive
    @Schema(description = "Student id", example = "1")
    Long studentId,

    @NotNull
    @Positive
    @Schema(description = "Route id", example = "1")
    Long routeId,

    @Positive
    @Schema(description = "Trip id; omit it when the notification is not about a trip", example = "1", nullable = true)
    Long tripId,

    @NotNull
    @Schema(description = "Journey fact", example = "TRIP_STARTED")
    JourneyEvent journeyEvent,

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Notification title", example = "Viaje iniciado", maxLength = 120)
    String title,

    @NotBlank
    @Size(max = 2000)
    @Schema(description = "Notification message", example = "El conductor marcó el inicio del recorrido.", maxLength = 2000)
    String message,

    @Positive
    @Schema(description = "Recipient tutor profile id; omit it to address every tutor of the student", example = "1", nullable = true)
    Long recipientProfileId
) {
}
