package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.ResolveDataDeletionRequestCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Data Deletion Request Command Service
 */
public interface DataDeletionRequestCommandService {
    /**
     * Handle Request Student Data Deletion Command
     *
     * @param command The {@link RequestStudentDataDeletionCommand} Command
     * @return A {@link Result} containing the id of the created request on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(RequestStudentDataDeletionCommand command);

    /**
     * Handle Resolve Data Deletion Request Command
     *
     * @param command The {@link ResolveDataDeletionRequestCommand} Command
     * @return A {@link Result} containing the id of the resolved request on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(ResolveDataDeletionRequestCommand command);
}
