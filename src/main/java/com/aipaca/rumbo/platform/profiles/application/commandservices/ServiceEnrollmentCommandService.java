package com.aipaca.rumbo.platform.profiles.application.commandservices;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AcceptServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CancelServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.EndServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestServiceEnrollmentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface ServiceEnrollmentCommandService {
    Result<Long,ApplicationError> handle(RequestServiceEnrollmentCommand command);
    Result<Long,ApplicationError> handle(AcceptServiceEnrollmentCommand command);
    Result<Long,ApplicationError> handle(CancelServiceEnrollmentCommand command);
    Result<Long,ApplicationError> handle(EndServiceEnrollmentCommand command);
}
