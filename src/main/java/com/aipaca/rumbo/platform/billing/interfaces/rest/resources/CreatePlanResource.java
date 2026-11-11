package com.aipaca.rumbo.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "CreatePlanRequest", description = "Subscription plan to create")
public record CreatePlanResource(
        @NotBlank
        @Size(max = 255)
        @Schema(example = "Basic")
        String name,

        @NotBlank
        @Size(max = 1000)
        @Schema(example = "Plan for a driver with one vehicle")
        String description,

        @NotNull
        @Schema(example = "29.90", minimum = "0")
        BigDecimal referencePrice,

        @NotBlank
        @Schema(description = "ISO 4217 currency code", example = "PEN")
        String currency,

        @Schema(example = "1", minimum = "1")
        Integer billingPeriodMonths,

        @Schema(example = "1", minimum = "0")
        Integer vehicleLimit,

        @NotNull
        @Schema(example = "true")
        Boolean active
) {
}