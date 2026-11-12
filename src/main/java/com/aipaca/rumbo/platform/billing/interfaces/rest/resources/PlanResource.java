package com.aipaca.rumbo.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Subscription plan and its commercial conditions")
public record PlanResource(
        Long id,
        String name,
        String description,
        BigDecimal referencePrice,
        String currency,
        Integer billingPeriodMonths,
        Integer vehicleLimit,
        boolean active
) {
}