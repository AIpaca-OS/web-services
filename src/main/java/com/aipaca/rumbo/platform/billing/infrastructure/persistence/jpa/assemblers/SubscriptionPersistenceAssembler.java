package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;

public class SubscriptionPersistenceAssembler {

    public static DriverSubscription toDomainFromPersistence(SubscriptionPersistenceEntity entity) {
        return DriverSubscription.reconstitute(
                entity.getId(), entity.getDriverId(), entity.getPlanId(),
                entity.getStartDate(), entity.getEndDate(), entity.getStatus(),
                entity.getVehicleLimit());
    }

    public static SubscriptionPersistenceEntity toPersistenceFromDomain(
            DriverSubscription subscription, Long previousSubscriptionId) {
        var entity = new SubscriptionPersistenceEntity();
        entity.setDriverId(subscription.getDriverId());
        entity.setPlanId(subscription.getPlanId());
        entity.setStartDate(subscription.getStartDate());
        entity.setEndDate(subscription.getEndDate());
        entity.setStatus(subscription.getStatus());
        entity.setVehicleLimit(subscription.getVehicleLimit());
        entity.setActivationKey("%d:%d:%d".formatted(
                subscription.getDriverId(), subscription.getPlanId(), previousSubscriptionId));
        return entity;
    }
}