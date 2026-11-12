package com.aipaca.rumbo.routes.domain.model.commands;

import com.aipaca.rumbo.routes.domain.model.RouteAssignment;
import java.util.List;
import java.util.UUID;

/** Replace a route's student-to-stop assignments in one transaction. */
public record ReplaceRouteAssignmentsCommand(
        UUID routeId, List<RouteAssignment> assignments, String authorizationHeader) {}
