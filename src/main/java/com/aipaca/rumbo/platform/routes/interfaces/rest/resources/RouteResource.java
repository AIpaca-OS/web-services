package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteShift;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Resource for a route.
 */
@Schema(name = "RouteResponse", description = "Route information response")
public record RouteResource(
    @Schema(description = "Route unique identifier", example = "1")
    Long id,

    @Schema(description = "Id of the driver profile responsible for the route", example = "2")
    Long driverProfileId,

    @Schema(description = "Route name", example = "Ruta Surco")
    String name,

    @Schema(description = "Route business code", example = "R-1A2B3C4D")
    String code,

    @Schema(description = "First departure time of the schedule", example = "07:00")
    String departureTime,

    @Schema(description = "Route status", example = "CONFIGURABLE")
    RouteStatus status,

    @Schema(description = "Route shift", example = "MORNING")
    RouteShift shift,

    @Schema(description = "Id of the vehicle that serves the route", example = "1", nullable = true)
    Long vehicleId,

    @Schema(description = "Number of stops", example = "1")
    int stopsCount,

    @Schema(description = "Ordered stops")
    List<RouteStopResource> stops,

    @Schema(description = "Weekly schedule")
    List<RouteScheduleResource> schedule,

    @Schema(description = "Whether the schedule is confirmed", example = "true")
    boolean scheduleConfirmed
) {
}
