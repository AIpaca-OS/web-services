
package com.aipaca.rumbo.platform.billing.domain.model.aggregates;

import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DriverSubscription
        extends AbstractDomainAggregateRoot<DriverSubscription> {

    private Long id;
    private final Long driverId;
    private final Long planId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private SubscriptionStatus status;
    private final Integer vehicleLimit;

    public DriverSubscription(
            ActivateSubscriptionCommand command,
            LocalDate startDate,
            LocalDate endDate,
            Integer vehicleLimit) {

        if (startDate == null || endDate == null
                || !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "Subscription end date must be after its start date");
        }

        this.driverId = command.driverId();
        this.planId = command.planId();
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.vehicleLimit = vehicleLimit;
    }

    public static DriverSubscription reconstitute(
            Long id,
            Long driverId,
            Long planId,
            LocalDate startDate,
            LocalDate endDate,
            SubscriptionStatus status,
            Integer vehicleLimit) {

        var command = new ActivateSubscriptionCommand(
                driverId, planId);

        var subscription = new DriverSubscription(
                command, startDate, endDate, vehicleLimit);

        subscription.id = id;
        subscription.status = status;

        return subscription;
    }

    /**
     * Cancels an active or suspended subscription.
     */
    public void cancel(LocalDate today) {
        if (status == SubscriptionStatus.CANCELLED) {
            return;
        }

        var currentStatus = statusOn(today);

        if (currentStatus != SubscriptionStatus.ACTIVE
                && currentStatus != SubscriptionStatus.SUSPENDED) {
            throw new IllegalStateException(
                    "Only active or suspended subscriptions can be cancelled");
        }

        this.status = SubscriptionStatus.CANCELLED;
    }

    /**
     * Pauses an active subscription.
     */
    public void pause(LocalDate today) {
        if (!isActiveOn(today)) {
            throw new IllegalStateException(
                    "Only active subscriptions can be paused");
        }

        this.status = SubscriptionStatus.SUSPENDED;
    }

    /**
     * Resumes a suspended subscription.
     */
    public void resume(LocalDate today) {
        if (statusOn(today) != SubscriptionStatus.SUSPENDED) {
            throw new IllegalStateException(
                    "Only valid suspended subscriptions can be resumed");
        }

        this.status = SubscriptionStatus.ACTIVE;
    }

    /**
     * Returns the subscription lifecycle status on a given date.
     */
    public SubscriptionStatus statusOn(LocalDate date) {
        if ((status == SubscriptionStatus.ACTIVE
                || status == SubscriptionStatus.SUSPENDED)
                && !date.isBefore(endDate)) {
            return SubscriptionStatus.EXPIRED;
        }

        return status;
    }

    public boolean isActiveOn(LocalDate date) {
        return !date.isBefore(startDate)
                && statusOn(date) == SubscriptionStatus.ACTIVE;
    }
}
