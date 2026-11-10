package com.aipaca.rumbo.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public record ActivateSubscriptionResource(
        @Schema(example = "1", minimum = "1") Long driverId,
        @Schema(example = "1", minimum = "1") Long planId
) {
    public ActivateSubscriptionResource {
        if (driverId == null || driverId < 1)
            throw new IllegalArgumentException("Driver id must be positive");

        if (planId == null || planId < 1)
            throw new IllegalArgumentException("Plan id must be positive");
    }
}