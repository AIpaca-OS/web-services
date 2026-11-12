package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteShift;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Resource for updating a route.
 */
@Schema(
    name = "UpdateRouteRequest",
    description = "Request payload for updating a route. Existing stops keep their id; new stops omit it.",
    example = "{\"name\": \"Ruta Surco actualizada\", \"shift\": \"MORNING\", \"vehicleId\": 1, \"stops\": [{\"id\": 1, \"address\": \"Av. Primavera 123\"}, {\"address\": \"Jr. Las Flores 456\"}], \"schedule\": [], \"scheduleConfirmed\": false}"
)
public record UpdateRouteResource(
    @NotBlank
    @Schema(description = "Route name", example = "Ruta Surco actualizada", maxLength = 160)
    String name,

    @NotNull
    @Schema(description = "Route shift", example = "MORNING")
    RouteShift shift,

    @Positive
    @Schema(description = "Id of the vehicle that serves the route", example = "1", nullable = true)
    Long vehicleId,

    @Schema(description = "Ordered stops of the route")
    List<@Valid UpdateRouteStopResource> stops,

    @Schema(description = "Weekly schedule; each day can appear once")
    List<@Valid RouteScheduleResource> schedule,

    @Schema(description = "Whether the schedule is confirmed", example = "false")
    Boolean scheduleConfirmed
) {
}
