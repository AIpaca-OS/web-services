package com.aipaca.rumbo.platform.routes.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Resource for a stop of a new route.
 */
@Schema(name = "CreateRouteStopRequest", description = "Stop of a new route")
public record CreateRouteStopResource(
    @NotBlank
    @Schema(description = "Stop address", example = "Av. Primavera 123", maxLength = 350)
    String address,

    @Positive
    @Schema(description = "Id of the school served at the stop", example = "1", nullable = true)
    Long schoolId
) {
}
