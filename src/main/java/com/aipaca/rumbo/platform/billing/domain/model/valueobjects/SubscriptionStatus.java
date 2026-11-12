package com.aipaca.rumbo.platform.billing.domain.model.valueobjects;

/**
 * Lifecycle status of a driver subscription.
 */
public enum SubscriptionStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    EXPIRED,
    CANCELLED
}