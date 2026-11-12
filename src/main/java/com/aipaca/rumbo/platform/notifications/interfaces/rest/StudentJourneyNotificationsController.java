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
import com.aipaca.rumbo.platform.notifications.interfaces.rest.resources.CreateStudentJourneyNotificationResource;
import com.aipaca.rumbo.platform.notifications.interfaces.rest.transform.CreateStudentJourneyNotificationCommandFromResourceAssembler;

/**
 * REST controller that notifies the tutors of a student about its journey.
 */
@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@RequestMapping(value = "/api/v1/notifications/student-journeys", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notification management endpoints")
public class StudentJourneyNotificationsController {
    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    /**
     * Constructor
     *
     * @param notificationCommandService The {@link NotificationCommandService} instance
     * @param notificationQueryService   The {@link NotificationQueryService} instance
     */
    public StudentJourneyNotificationsController(NotificationCommandService notificationCommandService,
                                  NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Notify a student journey fact", description = "Creates a STUDENT_JOURNEY notification for the tutors of a student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created",
                    content = @Content(schema = @Schema(implementation = NotificationResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "The student already has this notification for the trip")
    })
    public ResponseEntity<?> createStudentJourneyNotification(@Valid @RequestBody CreateStudentJourneyNotificationResource resource) {
        var command = CreateStudentJourneyNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = notificationCommandService.handle(command)
                .flatMap(id -> notificationQueryService.handle(new GetNotificationByIdQuery(id))
                        .<Result<Notification, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Notification", id.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, NotificationResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
