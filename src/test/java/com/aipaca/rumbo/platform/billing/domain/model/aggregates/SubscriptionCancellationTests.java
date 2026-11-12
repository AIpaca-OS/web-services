package com.aipaca.rumbo.platform.billing.domain.model.aggregates;

import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionCancellationTests {

    @Test
    void cancelActiveSubscription() {
        var today = LocalDate.of(2026, 11, 12);

        var subscription = new DriverSubscription(
                new ActivateSubscriptionCommand(1L, 1L),
                today,
                today.plusMonths(1),
                null);

        subscription.cancel(today);

        assertEquals(SubscriptionStatus.CANCELLED,
                subscription.getStatus());
        assertFalse(subscription.isActiveOn(today));
    }

    @Test
    void cancellingTwiceKeepsCancelledStatus() {
        var today = LocalDate.of(2026, 11, 12);

        var subscription = new DriverSubscription(
                new ActivateSubscriptionCommand(1L, 1L),
                today,
                today.plusMonths(1),
                null);

        subscription.cancel(today);
        subscription.cancel(today);

        assertEquals(SubscriptionStatus.CANCELLED,
                subscription.getStatus());
    }

    @Test
    void expiredSubscriptionCannotBeCancelled() {
        var start = LocalDate.of(2026, 1, 1);

        var subscription = new DriverSubscription(
                new ActivateSubscriptionCommand(1L, 1L),
                start,
                start.plusMonths(1),
                null);

        assertThrows(IllegalStateException.class,
                () -> subscription.cancel(start.plusMonths(2)));
    }
}
