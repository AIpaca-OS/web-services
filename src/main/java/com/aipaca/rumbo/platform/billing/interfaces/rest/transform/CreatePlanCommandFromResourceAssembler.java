package com.aipaca.rumbo.platform.billing.interfaces.rest.transform;

import com.aipaca.rumbo.platform.billing.domain.model.commands.CreatePlanCommand;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.CreatePlanResource;

public class CreatePlanCommandFromResourceAssembler {

    public static CreatePlanCommand toCommandFromResource(CreatePlanResource resource) {
        return new CreatePlanCommand(
                resource.name(),
                resource.description(),
                resource.referencePrice(),
                resource.currency(),
                resource.billingPeriodMonths(),
                resource.vehicleLimit(),
                resource.active()
        );
    }
}