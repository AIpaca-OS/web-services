package com.aipaca.rumbo.incidents.interfaces.rest.resources;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.DelayStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resource for a delay.
 */
@Schema(name = "DelayResponse", description = "Delay information response")
public record DelayResource(
    @Schema(description = "Delay unique identifier", example = "1")
    Long id,

    @Schema(description = "Trip id", example = "1")
    Long tripId,

    @Schema(description = "Id of the driver profile that reported the delay", example = "2")
    Long reportedByProfileId,

    @Schema(description = "Delay cause", example = "Tráfico intenso en la avenida principal")
    String cause,

    @Schema(description = "Estimated delay in minutes", example = "15")
    int estimatedMinutes,

    @Schema(description = "Delay status", example = "ACTIVE")
    DelayStatus status,

    @Schema(description = "Id of the delay that this one updates", nullable = true)
    Long previousDelayId,

    @Schema(description = "Report date")
    Instant reportedAt
) {
}
