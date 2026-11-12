package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for changing the status of a route. Only publication is supported.
 */
@Schema(name = "UpdateRouteStatusRequest", description = "Route status change", example = "{\"status\": \"PUBLISHED\"}")
public record UpdateRouteStatusResource(
    @NotNull
    @Schema(description = "Target status", example = "PUBLISHED")
    RouteStatus status
) {
}
