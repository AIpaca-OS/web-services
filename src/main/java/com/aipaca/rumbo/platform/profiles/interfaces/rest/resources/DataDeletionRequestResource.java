package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
/** HTTP-only representation; never serialize aggregate objects directly. */
public record DataDeletionRequestResource(
        Long id,
        Long studentId,
        Long requestedByProfileId,
        DeletionRequestStatus status,
        Instant requestedAt,
        Instant resolvedAt,
        String resolutionNote
) { }
