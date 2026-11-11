package com.aipaca.rumbo.platform.billing.application.queryservices;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.queries.GetDriverSubscriptionsQuery;

import java.util.List;

public interface SubscriptionQueryService {

    List<DriverSubscription> handle(GetDriverSubscriptionsQuery query);
}