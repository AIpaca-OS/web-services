package com.aipaca.rumbo.notifications.domain.model.commands;

import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationPriority;
import com.aipaca.rumbo.notifications.domain.model.valueobjects.NotificationType;
import java.util.UUID;

public record CreateNotificationCommand(NotificationType type, NotificationPriority priority,
                                        String title, String message, UUID routeId, Long studentId) {}
