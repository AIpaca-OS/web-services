package com.aipaca.rumbo.platform.billing.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CancelSubscriptionResource(
        @NotNull
        @Positive
        Long driverId
) {
}
