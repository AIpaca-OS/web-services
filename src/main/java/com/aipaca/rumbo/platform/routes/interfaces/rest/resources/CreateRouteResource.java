package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteShift;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Resource for creating a route.
 */
@Schema(
    name = "CreateRouteRequest",
    description = "Request payload for creating a configurable route",
    example = "{\"driverProfileId\": 2, \"name\": \"Ruta Surco\", \"shift\": \"MORNING\", \"vehicleId\": 1, \"stops\": [{\"address\": \"Av. Primavera 123\", \"schoolId\": null}], \"schedule\": [{\"dayOfWeek\": \"MONDAY\", \"departureTime\": \"07:00\"}], \"scheduleConfirmed\": true}"
)
public record CreateRouteResource(
    @NotNull
    @Positive
    @Schema(description = "Id of the driver profile responsible for the route", example = "2")
    Long driverProfileId,

    @NotBlank
    @Schema(description = "Route name", example = "Ruta Surco", maxLength = 160)
    String name,

    @NotNull
    @Schema(description = "Route shift", example = "MORNING")
    RouteShift shift,

    @Positive
    @Schema(description = "Id of the vehicle that serves the route", example = "1", nullable = true)
    Long vehicleId,

    @Schema(description = "Ordered stops of the route")
    List<@Valid CreateRouteStopResource> stops,

    @Schema(description = "Weekly schedule; each day can appear once")
    List<@Valid RouteScheduleResource> schedule,

    @Schema(description = "Whether the schedule is confirmed", example = "true")
    Boolean scheduleConfirmed
) {
}
