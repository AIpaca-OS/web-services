package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for a school.
 */
@Schema(
    name = "SchoolResponse",
    description = "School information response"
)
public record SchoolResource(
    @Schema(description = "School unique identifier", example = "1")
    Long id,

    @Schema(description = "School name", example = "Colegio San Juan")
    String name,

    @Schema(description = "School address", example = "Av. Principal 123")
    String address,

    @Schema(description = "School district", example = "Surco")
    String district
) {
}
