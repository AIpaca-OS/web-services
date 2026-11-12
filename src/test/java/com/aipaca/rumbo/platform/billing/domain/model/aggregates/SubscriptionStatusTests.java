
package com.aipaca.rumbo.platform.billing.domain.model.aggregates;

import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionStatusTests {

    private DriverSubscription newSubscription() {
        var today = LocalDate.of(2026, 11, 12);

        return new DriverSubscription(
                new ActivateSubscriptionCommand(1L, 1L),
                today,
                today.plusMonths(1),
                null
        );
    }

    @Test
    void shouldPauseActiveSubscription() {
        var subscription = newSubscription();
        var today = LocalDate.of(2026, 11, 12);

        subscription.pause(today);

        assertEquals(
                SubscriptionStatus.SUSPENDED,
                subscription.getStatus()
        );

        assertFalse(subscription.isActiveOn(today));
    }

    @Test
    void shouldResumeSuspendedSubscription() {
        var subscription = newSubscription();
        var today = LocalDate.of(2026, 11, 12);

        subscription.pause(today);
        subscription.resume(today);

        assertEquals(
                SubscriptionStatus.ACTIVE,
                subscription.getStatus()
        );

        assertTrue(subscription.isActiveOn(today));
    }

    @Test
    void shouldCancelSuspendedSubscription() {
        var subscription = newSubscription();
        var today = LocalDate.of(2026, 11, 12);

        subscription.pause(today);
        subscription.cancel(today);

        assertEquals(
                SubscriptionStatus.CANCELLED,
                subscription.getStatus()
        );
    }
}
