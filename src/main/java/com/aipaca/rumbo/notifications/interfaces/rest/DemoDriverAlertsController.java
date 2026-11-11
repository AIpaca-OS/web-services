package com.aipaca.rumbo.notifications.interfaces.rest;

import com.aipaca.rumbo.notifications.application.internal.NotificationService;
import com.aipaca.rumbo.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import com.aipaca.rumbo.routes.domain.model.RouteStatus;
import com.aipaca.rumbo.routes.domain.repositories.RouteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * A PUBLIC LOCAL DEMO ONLY. The notice will be visible to all demo roles.
 * NEVER deploy this controller without IAM, authenticated driver ownership
 * and authorized tutor filtering in the real Alerting context.
 */
@RestController
@RequestMapping("/api/v1/routes/{routeId}/demo-notices")
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
@Tag(name = "Demo Driver Notices", description = "Local non-personal notice sent by a demo driver")
public class DemoDriverAlertsController {
    private final RouteRepository routes;
    private final NotificationService notifications;

    public DemoDriverAlertsController(RouteRepository routes, NotificationService notifications) {
        this.routes = routes;
        this.notifications = notifications;
    }

    public record DemoAlertRequest(@NotBlank String kind,
                                   @NotBlank @Size(min = 4, max = 240) String message) {}
    public record DemoAlertResult(Long id, String title, String message) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Report a delay or incident on a published route (local demo only)")
    public DemoAlertResult create(@PathVariable UUID routeId, @Valid @RequestBody DemoAlertRequest request) {
        var route = routes.findById(routeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route does not exist"));
        if (route.getStatus() != RouteStatus.PUBLISHED)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Notices are permitted only for published routes");
        String title;
        NotificationType type;
        NotificationPriority priority;
        switch (request.kind().trim().toUpperCase(java.util.Locale.ROOT)) {
            case "DELAY" -> {
                title = "Retraso comunicado (demo) - " + route.getCode();
                type = NotificationType.warning;
                priority = NotificationPriority.regular;
            }
            case "INCIDENT" -> {
                title = "Incidencia comunicada (demo) - " + route.getCode();
                type = NotificationType.important;
                priority = NotificationPriority.critical;
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "kind must be DELAY or INCIDENT");
        }
        // No student identifiers, guardian details or addresses in this notice.
        var notice = notifications.create(new CreateNotificationCommand(
                type, priority, title, request.message().trim(), routeId, null));
        return new DemoAlertResult(notice.getId(), notice.getTitle(), notice.getMessage());
    }
}
