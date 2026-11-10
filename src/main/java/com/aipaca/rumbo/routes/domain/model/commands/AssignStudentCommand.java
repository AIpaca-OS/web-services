package com.aipaca.rumbo.routes.domain.model.commands;

import java.util.UUID;

/** Assign a student only after Profiles verifies the authenticated driver. */
public record AssignStudentCommand(UUID routeId, UUID stopId, String studentId, String authorizationHeader) {}
