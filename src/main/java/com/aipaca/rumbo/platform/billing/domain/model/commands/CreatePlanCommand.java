package com.aipaca.rumbo.platform.billing.domain.model.commands;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Command to create a subscription plan.
 *
 * @param name The plan name.
 * @param description The plan description.
 * @param referencePrice The reference price.
 * @param currency The ISO 4217 currency code.
 * @param billingPeriodMonths The billing period, when defined.
 * @param vehicleLimit The vehicle limit, when defined.
 * @param active Whether the plan is available.
 */
public record CreatePlanCommand(
        String name,
        String description,
        BigDecimal referencePrice,
        String currency,
        Integer billingPeriodMonths,
        Integer vehicleLimit,
        boolean active
) {
    /**
     * Validates the information required to create a plan.
     */
    public CreatePlanCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Plan name cannot be null or blank");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Plan description cannot be null or blank");
        }

        if (referencePrice == null || referencePrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "Reference price cannot be null or negative");
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                    "Currency cannot be null or blank");
        }

        Currency.getInstance(currency);

        if (billingPeriodMonths != null && billingPeriodMonths < 1) {
            throw new IllegalArgumentException(
                    "Billing period must be greater than zero");
        }

        if (vehicleLimit != null && vehicleLimit < 0) {
            throw new IllegalArgumentException(
                    "Vehicle limit cannot be negative");
        }
    }
}