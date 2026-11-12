package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for assigning a student to a stop of a route.
 */
@Schema(
    name = "AssignStudentRequest",
    description = "Request payload for assigning a student to a route stop",
    example = "{\"studentId\": 1, \"stopId\": 1}"
)
public record AssignStudentResource(
    @NotNull
    @Positive
    @Schema(description = "Student id (Profiles bounded context)", example = "1")
    Long studentId,

    @NotNull
    @Positive
    @Schema(description = "Id of a stop of the route", example = "1")
    Long stopId
) {
}
