package com.aipaca.rumbo.notifications.interfaces.rest;

import com.aipaca.rumbo.notifications.application.commandservices.NotificationCommandService;
import com.aipaca.rumbo.notifications.application.queryservices.NotificationQueryService;
import com.aipaca.rumbo.notifications.domain.model.commands.DeleteNotificationCommand;
import com.aipaca.rumbo.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.aipaca.rumbo.notifications.interfaces.rest.resources.CreateNotificationResource;
import com.aipaca.rumbo.notifications.interfaces.rest.resources.NotificationResource;
import com.aipaca.rumbo.notifications.interfaces.rest.transform.CreateNotificationCommandFromResourceAssembler;
import com.aipaca.rumbo.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import com.aipaca.rumbo.shared.application.result.ApplicationError;
import com.aipaca.rumbo.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.aipaca.rumbo.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationsController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;


    public NotificationsController(
            NotificationCommandService notificationCommandService,
            NotificationQueryService notificationQueryService
    ) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new notification",
            description = "Creates a new notification associated with a route and a student."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Notification created successfully",
                    content = @Content(schema = @Schema(implementation = NotificationResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createNotification(@Valid @RequestBody CreateNotificationResource resource) {
        var createNotificationCommand =
                CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);

        var result = notificationCommandService.handle(createNotificationCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                NotificationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @Operation(
            summary = "Get all notifications",
            description = "Retrieves a list of all notifications in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications found",
                    content = @Content(schema = @Schema(implementation = NotificationResource.class))
            )
    })
    public ResponseEntity<List<NotificationResource>> getAllNotifications() {
        var notifications = notificationQueryService.handle(new GetAllNotificationsQuery());

        if (notifications.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        var notificationResources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(notificationResources);
    }

    @DeleteMapping("/{notificationId}")
    @Operation(
            summary = "Delete a notification",
            description = "Deletes a specific notification by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<?> deleteNotification(
            @PathVariable
            @Parameter(description = "Notification unique identifier", example = "1", required = true)
            Long notificationId
    ) {
        var deleteNotificationCommand = new DeleteNotificationCommand(notificationId);
        var result = notificationCommandService.handle(deleteNotificationCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                deleted -> deleted,
                HttpStatus.OK
        );
    }
}