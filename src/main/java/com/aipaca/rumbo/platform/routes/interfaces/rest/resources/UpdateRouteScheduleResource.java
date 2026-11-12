package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Resource for updating the weekly schedule of a route.
 */
@Schema(
    name = "UpdateRouteScheduleRequest",
    description = "Request payload for replacing the weekly schedule of a route",
    example = "{\"schedule\": [{\"dayOfWeek\": \"MONDAY\", \"departureTime\": \"07:00\"}], \"confirm\": true}"
)
public record UpdateRouteScheduleResource(
    @NotNull
    @Schema(description = "Weekly schedule; each day can appear once")
    List<@Valid RouteScheduleResource> schedule,

    @Schema(description = "Whether the schedule is confirmed", example = "true")
    boolean confirm
) {
}
