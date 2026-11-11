package com.aipaca.rumbo.platform.notifications.interfaces.rest;

import com.aipaca.rumbo.platform.notifications.application.internal.NotificationService;
import com.aipaca.rumbo.platform.notifications.domain.model.agreggates.Notification;
import com.aipaca.rumbo.platform.notifications.domain.model.commands.CreateNotificationCommand;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.platform.notifications.domain.model.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

/** Only general demo notices: personal student notifications require IAM later. */
@RestController
@RequestMapping("/api/v1/notifications")
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
@Tag(name = "Notifications", description = "Local Sprint 3 generic notifications demo")
public class NotificationsController {
    public record NewNotification(@NotBlank String type, @NotBlank String priority,
                                  @NotBlank String title, @NotBlank String message) {}
    public record NotificationView(Long id, String type, String priority, String title,
                                   String message, String createdAt, String readAt,
                                   boolean read, long routeId, Long studentId) {}
    private final NotificationService service;
    public NotificationsController(NotificationService service) { this.service=service; }
    private NotificationView view(Notification n) {
        return new NotificationView(n.getId(), n.getType().name(), n.getPriority().name(),
                n.getTitle(), n.getMessage(), n.getCreatedAt().toString(),
                n.getReadAt() == null ? "" : n.getReadAt().toString(),
                n.isRead(), 0, null);
    }
    @GetMapping
    @Operation(summary = "List generic local-demo notifications")
    public List<NotificationView> list() { return service.getAll().stream().map(this::view).toList(); }
    @GetMapping("/{id}")
    @Operation(summary = "Get generic local-demo notification")
    public NotificationView get(@PathVariable Long id) { return view(service.get(id)); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete generic local-demo notification")
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a generic demo notification")
    public NotificationView create(@Valid @RequestBody NewNotification request) {
        try {
            var type=NotificationType.valueOf(request.type().toLowerCase(Locale.ROOT));
            var priority=NotificationPriority.valueOf(request.priority().toLowerCase(Locale.ROOT));
            return view(service.create(new CreateNotificationCommand(type, priority,
                    request.title(), request.message(), null, null)));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type or priority");
        }
    }
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark demo notification read")
    public NotificationView markRead(@PathVariable Long id) { return view(service.markRead(id)); }
}
