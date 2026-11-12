package com.aipaca.rumbo.platform.trips.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Resource for cancelling a trip.
 */
@Schema(name = "CancelTripRequest", description = "Trip cancellation", example = "{\"reason\": \"Mechanical issue\"}")
public record CancelTripResource(
    @NotBlank
    @Size(max = 500)
    @Schema(description = "Cancellation reason", example = "Mechanical issue", maxLength = 500)
    String reason
) {
}
