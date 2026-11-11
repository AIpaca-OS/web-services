package com.aipaca.rumbo.platform.incidents.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Resource for reporting a delay.
 */
@Schema(
    name = "ReportDelayRequest",
    description = "Request payload for reporting a delay of a trip",
    example = "{\"tripId\": 1, \"reportedByProfileId\": 2, \"cause\": \"Tráfico intenso en la avenida principal\", \"estimatedMinutes\": 15}"
)
public record ReportDelayResource(
    @NotNull
    @Positive
    @Schema(description = "Trip id", example = "1")
    Long tripId,

    @NotNull
    @Positive
    @Schema(description = "Id of the driver profile that reports the delay", example = "2")
    Long reportedByProfileId,

    @NotBlank
    @Size(max = 240)
    @Schema(description = "Delay cause", example = "Tráfico intenso en la avenida principal", maxLength = 240)
    String cause,

    @NotNull
    @Min(1)
    @Max(600)
    @Schema(description = "Estimated delay in minutes", example = "15", minimum = "1", maximum = "600")
    Integer estimatedMinutes
) {
}
