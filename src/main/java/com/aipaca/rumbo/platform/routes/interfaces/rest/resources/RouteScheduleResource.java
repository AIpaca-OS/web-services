package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.DayOfWeek;

/**
 * Resource for a weekly schedule row of a route.
 */
@Schema(name = "RouteSchedule", description = "Departure time of a route on a day of the week")
public record RouteScheduleResource(
    @NotNull
    @Schema(description = "Service day", example = "MONDAY")
    DayOfWeek dayOfWeek,

    @NotBlank
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "must use the HH:mm format")
    @Schema(description = "Departure time in HH:mm format", example = "07:00")
    String departureTime
) {
}
