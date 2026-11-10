package com.aipaca.rumbo.platform.billing.application.internal.commandservices;

import com.aipaca.rumbo.platform.billing.application.commandservices.PlanCommandService;
import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.domain.model.commands.CreatePlanCommand;
import com.aipaca.rumbo.platform.billing.domain.repositories.PlanRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Application service that executes plan commands.
 */
@Service
public class PlanCommandServiceImpl implements PlanCommandService {

    private final PlanRepository planRepository;

    public PlanCommandServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreatePlanCommand command) {
        if (planRepository.existsByName(command.name()))
            return Result.failure(ApplicationError.conflict(
                    "Plan", "Name '%s' already exists".formatted(command.name())));

        var plan = new Plan(command);

        try {
            plan = planRepository.save(plan);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-plan", e.getMessage()));
        }

        return Result.success(plan.getId());
    }
}