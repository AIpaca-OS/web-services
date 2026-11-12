package com.aipaca.rumbo.platform.billing.interfaces.rest.resources;

import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChangeSubscriptionStatusResource(
        @NotNull
        @Positive
        Long driverId,

        @NotNull
        SubscriptionStatus status
) {
}
