package com.aipaca.rumbo.platform.billing.application.internal.commandservices;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ChangeSubscriptionPlanCommand;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.aipaca.rumbo.platform.billing.domain.repositories.PlanRepository;
import com.aipaca.rumbo.platform.billing.domain.repositories.SubscriptionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImportedPlanActivationTests {

    private final PlanRepository plans = mock(PlanRepository.class);
    private final SubscriptionRepository subscriptions = mock(SubscriptionRepository.class);
    private final SubscriptionCommandServiceImpl service =
            new SubscriptionCommandServiceImpl(plans, subscriptions);

    private Plan plan(long id, String name) {
        return Plan.reconstitute(id, name, "Course prototype plan",
                BigDecimal.valueOf(25), "PEN", null, null, true);
    }

    @Test
    void importedPremiumPlanCanBeActivatedForOneMonth() {
        when(plans.findById(2L)).thenReturn(Optional.of(plan(2L, "Premium")));
        when(subscriptions.findAllByDriverId(1L)).thenReturn(List.of());
        when(subscriptions.findLatest(1L, 2L)).thenReturn(Optional.empty());
        when(subscriptions.save(any(DriverSubscription.class), eq(0L)))
                .thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new ActivateSubscriptionCommand(1L, 2L));
        assertTrue(result.toOptional().isPresent());
        var subscription = result.toOptional().orElseThrow().subscription();
        assertEquals(LocalDate.now(ZoneId.of("America/Lima")).plusMonths(1),
                subscription.getEndDate());
    }

    @Test
    void changingToImportedPlusPlanKeepsOneMonthCycle() {
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        var current = DriverSubscription.reconstitute(
                11L, 1L, 2L, today, today.plusMonths(1),
                SubscriptionStatus.ACTIVE, null);

        when(subscriptions.findById(11L)).thenReturn(Optional.of(current));
        when(plans.findById(3L)).thenReturn(Optional.of(plan(3L, "Plus")));
        when(subscriptions.findAllByDriverId(1L)).thenReturn(List.of(current));
        when(subscriptions.findLatest(1L, 3L)).thenReturn(Optional.empty());
        when(subscriptions.update(any(DriverSubscription.class)))
                .thenAnswer(call -> call.getArgument(0));
        when(subscriptions.save(any(DriverSubscription.class), eq(0L)))
                .thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new ChangeSubscriptionPlanCommand(11L, 1L, 3L));
        assertTrue(result.toOptional().isPresent());
        assertEquals(today.plusMonths(1), result.toOptional().orElseThrow().getEndDate());
        assertEquals(SubscriptionStatus.CANCELLED, current.getStatus());
    }

    @Test
    void plansOutsideImportedCatalogStillRequireExplicitBillingPeriod() {
        when(plans.findById(4L)).thenReturn(Optional.of(plan(4L, "Custom")));
        when(subscriptions.findAllByDriverId(1L)).thenReturn(List.of());
        when(subscriptions.findLatest(1L, 4L)).thenReturn(Optional.empty());

        var result = service.handle(new ActivateSubscriptionCommand(1L, 4L));
        assertTrue(result.isFailure());
    }
}
