
package com.aipaca.rumbo.platform.billing.application.internal.commandservices;

import com.aipaca.rumbo.platform.billing.application.commandservices.SubscriptionCommandService;
import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.CancelSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ChangeSubscriptionPlanCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ChangeSubscriptionStatusCommand;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.aipaca.rumbo.platform.billing.domain.repositories.PlanRepository;
import com.aipaca.rumbo.platform.billing.domain.repositories.SubscriptionRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class SubscriptionCommandServiceImpl
        implements SubscriptionCommandService {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionCommandServiceImpl(
            PlanRepository planRepository,
            SubscriptionRepository subscriptionRepository) {

        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Activates a driver's subscription.
     */
    @Override
    public Result<Activation, ApplicationError> handle(
            ActivateSubscriptionCommand command) {

        var today = LocalDate.now(ZoneId.of("America/Lima"));

        var current = subscriptionRepository
                .findAllByDriverId(command.driverId())
                .stream()
                .filter(subscription -> {
                    var status = subscription.statusOn(today);

                    return status == SubscriptionStatus.ACTIVE
                            || status == SubscriptionStatus.SUSPENDED;
                })
                .findFirst();

        if (current.isPresent()) {
            var existing = current.get();

            if (existing.getPlanId().equals(command.planId())) {
                return Result.success(
                        new Activation(existing, false));
            }

            return Result.failure(ApplicationError.conflict(
                    "Subscription",
                    "Change or cancel the current subscription before activating another plan"));
        }

        var previous = subscriptionRepository.findLatest(
                command.driverId(), command.planId());

        var plan = planRepository.findById(command.planId());

        if (plan.isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "Plan", command.planId().toString()));
        }

        var selectedPlan = plan.get();
        var period = selectedPlan.getBillingPeriodMonths();

        if (!selectedPlan.isActive() || period == null) {
            return Result.failure(ApplicationError.conflict(
                    "Plan",
                    "Plan must be active and have a billing period"));
        }

        var previousId = previous
                .map(DriverSubscription::getId)
                .orElse(0L);

        var subscription = new DriverSubscription(
                command,
                today,
                today.plusMonths(period),
                selectedPlan.getVehicleLimit());

        try {
            var saved = subscriptionRepository.save(
                    subscription, previousId);

            return Result.success(new Activation(saved, true));

        } catch (Exception e) {
            var latest = subscriptionRepository.findLatest(
                    command.driverId(), command.planId());

            if (latest.isPresent()
                    && !latest.get().getId().equals(previousId)
                    && latest.get().statusOn(today)
                    == SubscriptionStatus.ACTIVE) {

                return Result.success(
                        new Activation(latest.get(), false));
            }

            return Result.failure(ApplicationError.unexpected(
                    "activate-subscription",
                    "Unable to save the subscription"));
        }
    }

    /**
     * Cancels a subscription while preserving its history.
     */
    @Override
    @Transactional
    public Result<DriverSubscription, ApplicationError> handle(
            CancelSubscriptionCommand command) {

        var existing = subscriptionRepository.findById(
                command.subscriptionId());

        if (existing.isEmpty()
                || !existing.get().getDriverId()
                .equals(command.driverId())) {

            return Result.failure(ApplicationError.notFound(
                    "Subscription",
                    command.subscriptionId().toString()));
        }

        var subscription = existing.get();

        if (subscription.getStatus()
                == SubscriptionStatus.CANCELLED) {
            return Result.success(subscription);
        }

        var today = LocalDate.now(ZoneId.of("America/Lima"));
        var currentStatus = subscription.statusOn(today);

        if (currentStatus != SubscriptionStatus.ACTIVE
                && currentStatus != SubscriptionStatus.SUSPENDED) {

            return Result.failure(ApplicationError.conflict(
                    "Subscription",
                    "Only active or suspended subscriptions can be cancelled"));
        }

        subscription.cancel(today);

        return Result.success(
                subscriptionRepository.update(subscription));
    }

    /**
     * Changes a subscription's plan and preserves its history.
     */
    @Override
    @Transactional
    public Result<DriverSubscription, ApplicationError> handle(
            ChangeSubscriptionPlanCommand command) {

        var existing = subscriptionRepository.findById(
                command.subscriptionId());

        if (existing.isEmpty()
                || !existing.get().getDriverId()
                .equals(command.driverId())) {

            return Result.failure(ApplicationError.notFound(
                    "Subscription",
                    command.subscriptionId().toString()));
        }

        var current = existing.get();
        var today = LocalDate.now(ZoneId.of("America/Lima"));

        if (!current.isActiveOn(today)) {
            return Result.failure(ApplicationError.conflict(
                    "Subscription",
                    "Only active subscriptions can change plans"));
        }

        if (current.getPlanId().equals(command.newPlanId())) {
            return Result.success(current);
        }

        var target = planRepository.findById(command.newPlanId());

        if (target.isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "Plan", command.newPlanId().toString()));
        }

        var selectedPlan = target.get();

        if (!selectedPlan.isActive()
                || selectedPlan.getBillingPeriodMonths() == null) {

            return Result.failure(ApplicationError.conflict(
                    "Plan",
                    "Plan must be active and have a billing period"));
        }

        var anotherCurrent = subscriptionRepository
                .findAllByDriverId(command.driverId())
                .stream()
                .anyMatch(subscription ->
                        !subscription.getId().equals(current.getId())
                                && (subscription.statusOn(today)
                                == SubscriptionStatus.ACTIVE
                                || subscription.statusOn(today)
                                == SubscriptionStatus.SUSPENDED));

        if (anotherCurrent) {
            return Result.failure(ApplicationError.conflict(
                    "Subscription",
                    "Another subscription is already active or suspended"));
        }

        var previousTarget = subscriptionRepository.findLatest(
                command.driverId(), command.newPlanId());

        current.cancel(today);
        subscriptionRepository.update(current);

        var next = new DriverSubscription(
                new ActivateSubscriptionCommand(
                        command.driverId(), command.newPlanId()),
                today,
                today.plusMonths(
                        selectedPlan.getBillingPeriodMonths()),
                selectedPlan.getVehicleLimit());

        var previousId = previousTarget
                .map(DriverSubscription::getId)
                .orElse(0L);

        var saved = subscriptionRepository.save(
                next, previousId);

        return Result.success(saved);
    }

    /**
     * Pauses or resumes an existing subscription.
     */
    @Override
    @Transactional
    public Result<DriverSubscription, ApplicationError> handle(
            ChangeSubscriptionStatusCommand command) {

        var existing = subscriptionRepository.findById(
                command.subscriptionId());

        if (existing.isEmpty()
                || !existing.get().getDriverId()
                .equals(command.driverId())) {

            return Result.failure(ApplicationError.notFound(
                    "Subscription",
                    command.subscriptionId().toString()));
        }

        var subscription = existing.get();
        var today = LocalDate.now(ZoneId.of("America/Lima"));

        if (subscription.statusOn(today) == command.status()) {
            return Result.success(subscription);
        }

        try {
            switch (command.status()) {
                case SUSPENDED -> subscription.pause(today);
                case ACTIVE -> subscription.resume(today);
                default -> throw new IllegalArgumentException(
                        "Unsupported subscription status");
            }
        } catch (IllegalStateException exception) {
            return Result.failure(ApplicationError.conflict(
                    "Subscription", exception.getMessage()));
        }

        return Result.success(
                subscriptionRepository.update(subscription));
    }
}
