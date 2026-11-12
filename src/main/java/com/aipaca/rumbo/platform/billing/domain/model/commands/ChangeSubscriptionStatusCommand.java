package com.aipaca.rumbo.platform.billing.domain.model.commands;

import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;

/**
 * Command to pause or resume a driver subscription.
 */
public record ChangeSubscriptionStatusCommand(
        Long subscriptionId,
        Long driverId,
        SubscriptionStatus status
) {
    public ChangeSubscriptionStatusCommand {
        if (subscriptionId == null || subscriptionId < 1) {
            throw new IllegalArgumentException(
                    "Subscription id must be positive");
        }

        if (driverId == null || driverId < 1) {
            throw new IllegalArgumentException(
                    "Driver id must be positive");
        }

        if (status != SubscriptionStatus.ACTIVE &&
                status != SubscriptionStatus.SUSPENDED) {
            throw new IllegalArgumentException(
                    "Only ACTIVE and SUSPENDED are supported");
        }
    }
}
