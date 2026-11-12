package com.aipaca.rumbo.platform.billing.interfaces.rest.transform;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.PlanResource;

public class PlanResourceFromEntityAssembler {

    public static PlanResource toResourceFromEntity(Plan entity) {
        return new PlanResource(
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
}