package com.aipaca.rumbo.platform.billing.application.commandservices;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.DriverSubscription;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ActivateSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.CancelSubscriptionCommand;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ChangeSubscriptionPlanCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import com.aipaca.rumbo.platform.billing.domain.model.commands.ChangeSubscriptionStatusCommand;

public interface SubscriptionCommandService {

    Result<Activation, ApplicationError> handle(
            ActivateSubscriptionCommand command
    );

    Result<DriverSubscription, ApplicationError> handle(
            CancelSubscriptionCommand command
    );

    Result<DriverSubscription, ApplicationError> handle(
            ChangeSubscriptionPlanCommand command
    );

    record Activation(
            DriverSubscription subscription,
            boolean created
    ) {
    }

    Result<DriverSubscription, ApplicationError> handle(
            ChangeSubscriptionStatusCommand command
    );

}
