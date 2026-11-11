package com.aipaca.rumbo.platform.billing.interfaces.rest.transform;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.interfaces.rest.resources.SubscriptionResource;

import java.time.LocalDate;

public class SubscriptionResourceFromEntityAssembler {

    public static SubscriptionResource toResourceFromEntity(DriverSubscription entity, LocalDate today) {
        var active = entity.isActiveOn(today);
        Integer vehicleLimit = active ? entity.getVehicleLimit() : Integer.valueOf(0);

        return new SubscriptionResource(
                entity.getId(), entity.getDriverId(), entity.getPlanId(),
                entity.statusOn(today), entity.getStartDate(), entity.getEndDate(),
                active, vehicleLimit);
    }
}