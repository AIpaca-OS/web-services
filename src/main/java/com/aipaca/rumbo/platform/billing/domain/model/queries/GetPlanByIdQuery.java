package com.aipaca.rumbo.platform.billing.domain.model.queries;

/**
 * Query to retrieve a subscription plan by its identifier.
 *
 * @param planId The plan identifier.
 */
public record GetPlanByIdQuery(Long planId) {

    public GetPlanByIdQuery {
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException(
                    "Plan id cannot be null or less than 1");
        }
    }
}