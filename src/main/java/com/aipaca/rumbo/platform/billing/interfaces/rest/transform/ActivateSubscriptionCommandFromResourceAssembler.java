package com.aipaca.rumbo.platform.billing.interfaces.rest.transform;

import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.ActivateSubscriptionResource;

public class ActivateSubscriptionCommandFromResourceAssembler {

    public static ActivateSubscriptionCommand toCommandFromResource(
            Long driverId, ActivateSubscriptionResource resource) {
        return new ActivateSubscriptionCommand(driverId, resource.planId());
    }
}