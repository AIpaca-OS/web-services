package com.aipaca.rumbo.platform.notifications.interfaces.rest;

import com.aipaca.rumbo.platform.notifications.application.commandservices.NotificationCommandService;
import com.aipaca.rumbo.platform.notifications.application.queryservices.NotificationQueryService;
import com.aipaca.rumbo.platform.notifications.domain.model.aggregates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.resources.NotificationResource;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.DeleteNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsByRecipientProfileIdQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsByStudentIdQuery;
import com.aipaca.rumbo.platform.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.resources.CreateNotificationResource;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.transform.CreateNotificationCommandFromResourceAssembler;
import com.aipaca.rumbo.platform.shared.interfaces.rest.resources.MessageResource;
import com.aipaca.rumbo.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

/**
 * REST controller that exposes notification endpoints.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationsController {
    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    /**
     * Constructor
     *
     * @param notificationCommandService The {@link NotificationCommandService} instance
     * @param notificationQueryService   The {@link NotificationQueryService} instance
     */
    public NotificationsController(NotificationCommandService notificationCommandService,
                                   NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a notification", description = "Creates a general notification, for every user or for one recipient profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created",
                    content = @Content(schema = @Schema(implementation = NotificationResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createNotification(@Valid @RequestBody CreateNotificationResource resource) {
        var command = CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        return toNotificationResponse(notificationCommandService.handle(command), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get notifications", description = "Retrieves every notification, or only those of a recipient profile or of a student, most recent first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications found",
                    content = @Content(schema = @Schema(implementation = NotificationResource.class)))
    })
    public ResponseEntity<List<NotificationResource>> getAllNotifications(
            @RequestParam(required = false)
            @Parameter(description = "Student unique identifier; when present, only the notifications of that student", example = "1")
            Long studentId,
            @RequestParam(required = false)
            @Parameter(description = "Recipient profile unique identifier; when present, only the notifications addressed to that profile", example = "1")
            Long recipientProfileId) {
        List<Notification> notifications;
        if (recipientProfileId != null)
            notifications = notificationQueryService.handle(new GetAllNotificationsByRecipientProfileIdQuery(recipientProfileId));
        else if (studentId != null)
            notifications = notificationQueryService.handle(new GetAllNotificationsByStudentIdQuery(studentId));
        else
            notifications = notificationQueryService.handle(new GetAllNotificationsQuery());
        return ResponseEntity.ok(notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a notification by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found",
                    content = @Content(schema = @Schema(implementation = NotificationResource.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<?> getNotificationById(
            @PathVariable @Parameter(description = "Notification unique identifier", example = "1", required = true)
            Long notificationId) {
        var notification = notificationQueryService.handle(new GetNotificationByIdQuery(notificationId));
        if (notification.isEmpty()) {
            var error = ApplicationError.notFound("Notification", notificationId.toString());
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(notification.get()));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Records the first read date of a notification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read",
                    content = @Content(schema = @Schema(implementation = NotificationResource.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<?> markNotificationAsRead(
            @PathVariable @Parameter(description = "Notification unique identifier", example = "1", required = true)
            Long notificationId) {
        var command = new MarkNotificationAsReadCommand(notificationId);
        return toNotificationResponse(notificationCommandService.handle(command), HttpStatus.OK);
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Deletes a notification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<?> deleteNotification(
            @PathVariable @Parameter(description = "Notification unique identifier", example = "1", required = true)
            Long notificationId) {
        var result = notificationCommandService.handle(new DeleteNotificationCommand(notificationId))
                .map(id -> new MessageResource("Notification with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }

    private ResponseEntity<?> toNotificationResponse(Result<Long, ApplicationError> commandResult, HttpStatus status) {
        var result = commandResult.flatMap(id -> notificationQueryService.handle(new GetNotificationByIdQuery(id))
                .<Result<Notification, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Notification", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, NotificationResourceFromEntityAssembler::toResourceFromEntity, status);
    }
}
