package com.aipaca.rumbo.incidents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Resource for updating the estimation of a delay.
 */
@Schema(
    name = "UpdateDelayRequest",
    description = "Request payload for updating the estimation of an active delay",
    example = "{\"reportedByProfileId\": 2, \"cause\": \"Tráfico intenso en la avenida principal\", \"estimatedMinutes\": 25}"
)
public record UpdateDelayResource(
    @NotNull
    @Positive
    @Schema(description = "Id of the driver profile that reports the update", example = "2")
    Long reportedByProfileId,

    @NotBlank
    @Size(max = 240)
    @Schema(description = "Delay cause", example = "Tráfico intenso en la avenida principal", maxLength = 240)
    String cause,

    @NotNull
    @Min(1)
    @Max(600)
    @Schema(description = "New estimated delay in minutes", example = "25", minimum = "1", maximum = "600")
    Integer estimatedMinutes
) {
}
