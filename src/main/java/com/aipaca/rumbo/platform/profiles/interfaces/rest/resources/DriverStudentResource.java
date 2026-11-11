package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource for a student enrolled with a driver.
 */
@Schema(
    name = "DriverStudentResponse",
    description = "Student enrolled with a driver information response"
)
public record DriverStudentResource(
    @Schema(description = "Student unique identifier", example = "1")
    Long id,

    @Schema(description = "First name", example = "Lucía")
    String firstName,

    @Schema(description = "Last name", example = "Pérez")
    String lastName,

    @Schema(description = "School id")
    Long schoolId,

    @Schema(description = "Student status", example = "ACTIVE")
    StudentStatus status
) {
}
