
package com.aipaca.rumbo.platform.billing.domain.model.commands;

/**
 * Command to cancel a driver's subscription.
 */
public record CancelSubscriptionCommand(
        Long subscriptionId,
        Long driverId
) {
    public CancelSubscriptionCommand {
        if (subscriptionId == null || subscriptionId < 1) {
            throw new IllegalArgumentException(
                    "Subscription id must be positive");
        }

        if (driverId == null || driverId < 1) {
            throw new IllegalArgumentException(
                    "Driver id must be positive");
        }
    }
}
