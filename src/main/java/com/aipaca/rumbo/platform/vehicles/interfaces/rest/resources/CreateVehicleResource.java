package com.aipaca.rumbo.platform.vehicles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Resource for registering a vehicle.
 */
@Schema(
    name = "CreateVehicleRequest",
    description = "Request payload for registering a vehicle",
    example = "{\"driverProfileId\": 2, \"brand\": \"Toyota\", \"model\": \"Hiace\", \"licensePlate\": \"ABC-123\", \"capacity\": 15, \"year\": 2022}"
)
public record CreateVehicleResource(
    @NotNull
    @Positive
    @Schema(description = "Id of the driver profile that owns the vehicle", example = "2")
    Long driverProfileId,

    @NotBlank
    @Size(max = 80)
    @Schema(description = "Vehicle brand", example = "Toyota", maxLength = 80)
    String brand,

    @NotBlank
    @Size(max = 80)
    @Schema(description = "Vehicle model", example = "Hiace", maxLength = 80)
    String model,

    @NotBlank
    @Size(max = 20)
    @Schema(description = "License plate; spaces are removed and letters upper-cased", example = "ABC-123", maxLength = 20)
    String licensePlate,

    @NotNull
    @Min(1)
    @Max(80)
    @Schema(description = "Passenger capacity", example = "15", minimum = "1", maximum = "80")
    Integer capacity,

    @NotNull
    @Min(1980)
    @Max(2100)
    @Schema(description = "Manufacturing year", example = "2022", minimum = "1980", maximum = "2100")
    Integer year
) {
}
