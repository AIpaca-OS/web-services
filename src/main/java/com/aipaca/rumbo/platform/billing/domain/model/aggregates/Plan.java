package com.aipaca.rumbo.platform.billing.domain.model.aggregates;

import com.aipaca.rumbo.platform.billing.domain.model.commands.CreatePlanCommand;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Plan aggregate root.
 *
 * <p>
 * Represents the commercial conditions of a subscription plan
 * within Subscriptions and Billing.
 * </p>
 */
@Getter
public class Plan extends AbstractDomainAggregateRoot<Plan> {

    private Long id;
    private String name;
    private String description;
    private BigDecimal referencePrice;
    private String currency;
    private Integer billingPeriodMonths;
    private Integer vehicleLimit;
    private boolean active;

    /**
     * Creates a plan from a validated command.
     *
     * @param command The command containing the plan information.
     */
    public Plan(CreatePlanCommand command) {
        this.name = command.name();
        this.description = command.description();
        this.referencePrice = command.referencePrice();
        this.currency = command.currency();
        this.billingPeriodMonths = command.billingPeriodMonths();
        this.vehicleLimit = command.vehicleLimit();
        this.active = command.active();
    }

    /**
     * Reconstitutes an existing plan from persistence state.
     *
     * @return The reconstituted plan.
     */
    public static Plan reconstitute(
            Long id,
            String name,
            String description,
            BigDecimal referencePrice,
            String currency,
            Integer billingPeriodMonths,
            Integer vehicleLimit,
            boolean active
    ) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(
                    "Plan id cannot be null or less than 1");
        }

        var command = new CreatePlanCommand(
                name,
                description,
                referencePrice,
                currency,
                billingPeriodMonths,
                vehicleLimit,
                active
        );

        var plan = new Plan(command);
        plan.id = id;

        return plan;
    }
}