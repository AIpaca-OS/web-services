package com.aipaca.rumbo.platform.billing.application.commandservices;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

public interface SubscriptionCommandService {

    Result<Activation, ApplicationError> handle(ActivateSubscriptionCommand command);

    record Activation(DriverSubscription subscription, boolean created) {
    }
}