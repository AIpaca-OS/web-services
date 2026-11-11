package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Resource for a student.
 */
@Schema(
    name = "StudentResponse",
    description = "Student information response"
)
public record StudentResource(
    @Schema(description = "Student unique identifier", example = "1")
    Long id,

    @Schema(description = "First name", example = "Lucía")
    String firstName,

    @Schema(description = "Last name", example = "Pérez")
    String lastName,

    @Schema(description = "Birth date", example = "2016-05-03")
    LocalDate birthDate,

    @Schema(description = "School id")
    Long schoolId,

    @Schema(description = "Student status", example = "ACTIVE")
    StudentStatus status,

    @Schema(description = "Anonymization date")
    Instant anonymizedAt
) {
}
