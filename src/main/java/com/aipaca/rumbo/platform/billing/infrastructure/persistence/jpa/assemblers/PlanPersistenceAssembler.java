package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;

/**
 * Static assembler between plan domain and persistence representations.
 */
public final class PlanPersistenceAssembler {

    private PlanPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity The persistence entity.
     * @return The reconstituted plan.
     */
    public static Plan toDomainFromPersistence(
            PlanPersistenceEntity entity
    ) {
        if (entity == null) return null;

        return Plan.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getReferencePrice(),
                entity.getCurrency(),
                entity.getBillingPeriodMonths(),
                entity.getVehicleLimit(),
                entity.isActive()
        );
    }

    /**
     * Converts a domain aggregate into a persistence entity.
     *
     * @param plan The domain aggregate.
     * @return The persistence entity.
     */
    public static PlanPersistenceEntity toPersistenceFromDomain(
            Plan plan
    ) {
        if (plan == null) return null;

        var entity = new PlanPersistenceEntity();

        // New plans keep a null ID so JPA can generate it.
        if (plan.getId() != null) {
            entity.setId(plan.getId());
        }

        entity.setName(plan.getName());
        entity.setDescription(plan.getDescription());
        entity.setReferencePrice(plan.getReferencePrice());
        entity.setCurrency(plan.getCurrency());
        entity.setBillingPeriodMonths(plan.getBillingPeriodMonths());
        entity.setVehicleLimit(plan.getVehicleLimit());
        entity.setActive(plan.isActive());

        return entity;
    }
}