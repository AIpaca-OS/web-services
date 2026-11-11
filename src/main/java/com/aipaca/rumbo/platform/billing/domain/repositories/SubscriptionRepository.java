package com.aipaca.rumbo.platform.billing.domain.repositories;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {

    Optional<DriverSubscription> findLatest(Long driverId, Long planId);

    List<DriverSubscription> findAllByDriverId(Long driverId);

    DriverSubscription save(DriverSubscription subscription, Long previousSubscriptionId);
}