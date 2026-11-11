package com.aipaca.rumbo.platform.billing.application.internal.queryservices;

import com.aipaca.rumbo.platform.billing.application.queryservices.SubscriptionQueryService;
import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetDriverSubscriptionsQuery;
import com.aipaca.rumbo.platform.billing.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<DriverSubscription> handle(GetDriverSubscriptionsQuery query) {
        return subscriptionRepository.findAllByDriverId(query.driverId());
    }
}