package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Resource for a stop of an existing route.
 */
@Schema(name = "UpdateRouteStopRequest", description = "Stop of an existing route")
public record UpdateRouteStopResource(
    @Positive
    @Schema(description = "Id of an existing stop; omit it for a new stop", example = "1", nullable = true)
    Long id,

    @NotBlank
    @Schema(description = "Stop address", example = "Av. Primavera 123", maxLength = 350)
    String address,

    @Positive
    @Schema(description = "Id of the school served at the stop", example = "1", nullable = true)
    Long schoolId
) {
}
