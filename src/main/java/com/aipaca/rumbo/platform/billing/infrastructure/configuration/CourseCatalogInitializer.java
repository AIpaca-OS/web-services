package com.aipaca.rumbo.platform.billing.infrastructure.configuration;

import com.aipaca.rumbo.platform.billing.application.commandservices.PlanCommandService;
import com.aipaca.rumbo.platform.billing.domain.model.commands.CreatePlanCommand;
import com.aipaca.rumbo.platform.billing.domain.repositories.PlanRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Imports the course catalog from the prototype without overwriting existing plans. */
@Component
@ConditionalOnProperty(name = "rumbo.course.catalog.enabled", havingValue = "true")
class CourseCatalogInitializer implements ApplicationRunner {
    private static final List<CreatePlanCommand> PLANS = List.of(
            new CreatePlanCommand("Freemium",
                    "A simple option to start using Rumbo and explore the platform's main tools.",
                    BigDecimal.ZERO, "PEN", null, null, true),
            new CreatePlanCommand("Premium",
                    "Designed for drivers looking for a more complete experience to organize and review their trip information.",
                    BigDecimal.valueOf(25), "PEN", null, null, true),
            new CreatePlanCommand("Plus",
                    "An expanded option for drivers who want to make the most of the tools available in Rumbo.",
                    BigDecimal.valueOf(40), "PEN", null, null, true)
    );

    private final PlanCommandService commands;
    private final PlanRepository plans;

    CourseCatalogInitializer(PlanCommandService commands, PlanRepository plans) {
        this.commands = commands;
        this.plans = plans;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (var plan : PLANS) {
            if (plans.existsByName(plan.name())) {
                continue;
            }
            if (commands.handle(plan).isFailure() && !plans.existsByName(plan.name())) {
                throw new IllegalStateException("Could not import course plan " + plan.name());
            }
        }
    }
}
