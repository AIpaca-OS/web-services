package com.aipaca.rumbo.routes.domain.model.commands;

import java.util.UUID;

public record ConfirmStudentArrivalCommand(UUID tripId, String studentId) {
    public ConfirmStudentArrivalCommand {
        if (tripId == null || studentId == null || studentId.isBlank())
            throw new IllegalArgumentException("Trip and student are required");
    }
}
