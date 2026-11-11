package com.aipaca.rumbo.platform.billing.application.internal.queryservices;

import com.aipaca.rumbo.platform.billing.application.queryservices.PlanQueryService;
import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetAllPlansQuery;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetPlanByIdQuery;
import com.aipaca.rumbo.platform.billing.domain.repositories.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service that resolves plan read queries.
 */
@Service
public class PlanQueryServiceImpl implements PlanQueryService {

    private final PlanRepository planRepository;

    public PlanQueryServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Optional<Plan> handle(GetPlanByIdQuery query) {
        return planRepository.findById(query.planId());
    }

    @Override
    public List<Plan> handle(GetAllPlansQuery query) {
        return planRepository.findAll();
    }
}