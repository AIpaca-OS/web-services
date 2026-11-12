package com.aipaca.rumbo.platform.billing.domain.model.commands;

/**
 * Command to change an active subscription's plan.
 */
public record ChangeSubscriptionPlanCommand(
        Long subscriptionId,
        Long driverId,
        Long newPlanId
) {
    public ChangeSubscriptionPlanCommand {
        if (subscriptionId == null || subscriptionId < 1) {
            throw new IllegalArgumentException(
                    "Subscription id must be positive");
        }

        if (driverId == null || driverId < 1) {
            throw new IllegalArgumentException(
                    "Driver id must be positive");
        }

        if (newPlanId == null || newPlanId < 1) {
            throw new IllegalArgumentException(
                    "Plan id must be positive");
        }
    }
}
