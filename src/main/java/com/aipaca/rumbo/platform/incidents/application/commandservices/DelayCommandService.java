package com.aipaca.rumbo.platform.incidents.application.commandservices;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateDelayCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Delay Command Service. Every handler returns the id of the active delay.
 */
public interface DelayCommandService {
    Result<Long, ApplicationError> handle(ReportDelayCommand command);

    Result<Long, ApplicationError> handle(UpdateDelayCommand command);
}
