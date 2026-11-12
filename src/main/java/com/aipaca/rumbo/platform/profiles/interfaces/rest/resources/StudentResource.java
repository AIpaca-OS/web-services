package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
/** HTTP-only representation; never serialize aggregate objects directly. */
public record StudentResource(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Long schoolId,
        StudentStatus status,
        Instant anonymizedAt
) { }
