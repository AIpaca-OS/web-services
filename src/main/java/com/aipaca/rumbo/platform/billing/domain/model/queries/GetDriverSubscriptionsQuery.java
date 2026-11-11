package com.aipaca.rumbo.platform.billing.domain.model.queries;

public record GetDriverSubscriptionsQuery(Long driverId) {
    public GetDriverSubscriptionsQuery {
        if (driverId == null || driverId < 1)
            throw new IllegalArgumentException("Driver id cannot be null or less than 1");
    }
}