package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Resource for updating the information of a student.
 */
@Schema(
    name = "UpdateStudentRequest",
    description = "Request payload for updating the information of a student"
)
public record UpdateStudentResource(
    @NotBlank
    @Size(max = 100)
    @Schema(description = "First name", example = "Lucía")
    String firstName,

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Last name", example = "Pérez")
    String lastName,

    @NotNull
    @Past
    @Schema(description = "Birth date", example = "2016-05-03")
    LocalDate birthDate,

    @Positive
    @Schema(description = "School id, optional", example = "1")
    Long schoolId
) {
}
