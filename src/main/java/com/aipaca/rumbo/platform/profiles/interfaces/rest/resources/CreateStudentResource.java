package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Resource for registering a student.
 */
@Schema(
    name = "CreateStudentRequest",
    description = "Request payload for registering a student",
    example = "{\"tutorProfileId\": 1, \"firstName\": \"Lucía\", \"lastName\": \"Pérez\", \"birthDate\": \"2016-05-03\", \"schoolId\": null, \"relationshipType\": \"MOTHER\"}"
)
public record CreateStudentResource(
    @NotNull
    @Positive
    @Schema(description = "Id of the tutor profile that registers the student", example = "1")
    Long tutorProfileId,

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
    Long schoolId,

    @NotNull
    @Schema(description = "Relationship between the tutor and the student", example = "MOTHER")
    RelationshipType relationshipType
) {
}
