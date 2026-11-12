package com.aipaca.rumbo.platform.billing.application.commandservices;

import com.aipaca.rumbo.platform.billing.domain.model.commands.CreatePlanCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Application service contract for plan commands.
 */
public interface PlanCommandService {

    /**
     * Handles plan creation.
     *
     * @param command The initial plan information.
     * @return The created plan identifier or an application error.
     */
    Result<Long, ApplicationError> handle(CreatePlanCommand command);
}
