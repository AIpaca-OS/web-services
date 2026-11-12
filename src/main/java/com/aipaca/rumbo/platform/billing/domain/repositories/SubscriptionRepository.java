package com.aipaca.rumbo.platform.billing.domain.repositories;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.DriverProfileId;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {

    Optional<DriverSubscription> findLatest(DriverProfileId driverProfileId, Long planId);

    List<DriverSubscription> findAllByDriverProfileId(DriverProfileId driverProfileId);

    DriverSubscription save(DriverSubscription subscription, Long previousSubscriptionId);
}