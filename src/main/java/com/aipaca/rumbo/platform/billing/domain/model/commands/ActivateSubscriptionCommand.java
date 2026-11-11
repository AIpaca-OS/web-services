package com.aipaca.rumbo.platform.billing.domain.model.commands;

/**
 * Command to activate a subscription for a driver.
 */
public record ActivateSubscriptionCommand(Long driverId, Long planId) {

    public ActivateSubscriptionCommand {
        if (driverId == null || driverId < 1) {
            throw new IllegalArgumentException("Driver id cannot be null or less than 1");
        }

        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id cannot be null or less than 1");
        }
    }
}