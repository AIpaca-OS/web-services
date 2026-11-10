package com.aipaca.rumbo.notifications.domain.model.commands;

public record CreateNotificationCommand(String type, String priority, String title, String message, String readAt, boolean read, long routeId, Long studentId) {
}
