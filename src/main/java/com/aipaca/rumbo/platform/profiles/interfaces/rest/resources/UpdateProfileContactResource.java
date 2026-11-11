package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.*;
/** HTTP-only representation; never serialize aggregate objects directly. */
public record UpdateProfileContactResource(
        @NotBlank @Size(max=20) String phoneNumber
) { }
