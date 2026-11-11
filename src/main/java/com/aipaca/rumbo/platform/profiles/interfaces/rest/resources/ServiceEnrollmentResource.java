package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
/** HTTP-only representation; never serialize aggregate objects directly. */
public record ServiceEnrollmentResource(
        Long id,
        Long studentId,
        Long driverProfileId,
        Long requestedByProfileId,
        String status,
        Instant requestedAt,
        Instant acceptedAt,
        Instant cancelledAt,
        Instant endedAt
) { }
