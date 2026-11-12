package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Resource for a route stop.
 */
@Schema(name = "RouteStopResponse", description = "Route stop and the students assigned to it")
public record RouteStopResource(
    @Schema(description = "Stop unique identifier", example = "1")
    Long id,

    @Schema(description = "Stop address", example = "Av. Primavera 123")
    String address,

    @Schema(description = "Position of the stop in the route", example = "1")
    int position,

    @Schema(description = "Ids of the students assigned to the stop")
    List<Long> studentIds
) {
}
