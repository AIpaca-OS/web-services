package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.AcceptServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CancelServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.EndServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Service Enrollment Command Service
 */
public interface ServiceEnrollmentCommandService {
    /**
     * Handle Request Service Enrollment Command
     *
     * @param command The {@link RequestServiceEnrollmentCommand} Command
     * @return A {@link Result} containing the id of the requested enrollment on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(RequestServiceEnrollmentCommand command);

    /**
     * Handle Accept Service Enrollment Command
     *
     * @param command The {@link AcceptServiceEnrollmentCommand} Command
     * @return A {@link Result} containing the id of the accepted enrollment on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(AcceptServiceEnrollmentCommand command);

    /**
     * Handle Cancel Service Enrollment Command
     *
     * @param command The {@link CancelServiceEnrollmentCommand} Command
     * @return A {@link Result} containing the id of the cancelled enrollment on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(CancelServiceEnrollmentCommand command);

    /**
     * Handle End Service Enrollment Command
     *
     * @param command The {@link EndServiceEnrollmentCommand} Command
     * @return A {@link Result} containing the id of the ended enrollment on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(EndServiceEnrollmentCommand command);
}
