package com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.vehicles.domain.model.valueobjects.VehicleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for a vehicle.
 */
@Schema(name = "VehicleResponse", description = "Vehicle information response")
public record VehicleResource(
    @Schema(description = "Vehicle unique identifier", example = "1")
    Long id,

    @Schema(description = "Id of the driver profile that owns the vehicle", example = "2")
    Long driverProfileId,

    @Schema(description = "Vehicle brand", example = "Toyota")
    String brand,

    @Schema(description = "Vehicle model", example = "Hiace")
    String model,

    @Schema(description = "Normalized license plate", example = "ABC-123")
    String licensePlate,

    @Schema(description = "Passenger capacity", example = "15")
    Integer capacity,

    @Schema(description = "Manufacturing year", example = "2022")
    Integer year,

    @Schema(description = "Verification status", example = "PENDING_VERIFICATION")
    VehicleStatus status
) {
}
