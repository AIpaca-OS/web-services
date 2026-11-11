package com.aipaca.rumbo.platform.billing.application.internal.commandservices;

import com.aipaca.rumbo.platform.billing.application.commandservices.SubscriptionCommandService;
import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.aipaca.rumbo.platform.billing.domain.repositories.PlanRepository;
import com.aipaca.rumbo.platform.billing.domain.repositories.SubscriptionRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionCommandServiceImpl(PlanRepository planRepository,
                                          SubscriptionRepository subscriptionRepository) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Result<Activation, ApplicationError> handle(ActivateSubscriptionCommand command) {
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        var previous = subscriptionRepository.findLatest(command.driverId(), command.planId());

        if (previous.isPresent() && previous.get().statusOn(today) == SubscriptionStatus.ACTIVE)
            return Result.success(new Activation(previous.get(), false));

        var plan = planRepository.findById(command.planId());
        if (plan.isEmpty())
            return Result.failure(ApplicationError.notFound("Plan", command.planId().toString()));

        var selectedPlan = plan.get();
        var period = selectedPlan.getBillingPeriodMonths();
        if (!selectedPlan.isActive() || period == null)
            return Result.failure(ApplicationError.conflict(
                    "Plan", "Plan must be active and have a billing period"));

        var previousId = previous.map(DriverSubscription::getId).orElse(0L);
        var subscription = new DriverSubscription(
                command, today, today.plusMonths(period), selectedPlan.getVehicleLimit());

        try {
            var saved = subscriptionRepository.save(subscription, previousId);
            return Result.success(new Activation(saved, true));
        } catch (Exception e) {
            var latest = subscriptionRepository.findLatest(command.driverId(), command.planId());
            if (latest.isPresent()
                    && !latest.get().getId().equals(previousId)
                    && latest.get().statusOn(today) == SubscriptionStatus.ACTIVE)
                return Result.success(new Activation(latest.get(), false));

            return Result.failure(ApplicationError.unexpected(
                    "activate-subscription", "Unable to save the subscription"));
        }
    }
}