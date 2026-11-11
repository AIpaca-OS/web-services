package com.aipaca.rumbo.platform.billing.application.queryservices;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetAllPlansQuery;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetPlanByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for plan read queries.
 */
public interface PlanQueryService {

    /**
     * Retrieves a plan by its identifier.
     *
     * @param query The plan identifier query.
     * @return The matching plan, if found.
     */
    Optional<Plan> handle(GetPlanByIdQuery query);

    /**
     * Retrieves all plans.
     *
     * @param query The query marker.
     * @return The list of plans.
     */
    List<Plan> handle(GetAllPlansQuery query);
}