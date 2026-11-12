package com.aipaca.rumbo.platform.billing.interfaces.rest.resources;

import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.SubscriptionStatus;

import java.time.LocalDate;

public record SubscriptionResource(
        Long id,
        Long driverId,
        Long planId,
        SubscriptionStatus status,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        Integer vehicleLimit
) {
}