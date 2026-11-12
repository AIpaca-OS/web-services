package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for a route assignment.
 */
@Schema(name = "RouteAssignmentResponse", description = "Student assigned to a route stop")
public record RouteAssignmentResource(
    @Schema(description = "Assignment unique identifier", example = "1")
    Long id,

    @Schema(description = "Stop id", example = "1")
    Long stopId,

    @Schema(description = "Student id", example = "1")
    Long studentId
) {
}
