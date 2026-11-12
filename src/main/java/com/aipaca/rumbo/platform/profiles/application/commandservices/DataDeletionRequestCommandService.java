package com.aipaca.rumbo.platform.profiles.application.commandservices;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.ResolveDataDeletionRequestCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface DataDeletionRequestCommandService {
    Result<Long,ApplicationError> handle(RequestStudentDataDeletionCommand command);
    Result<Long,ApplicationError> handle(ResolveDataDeletionRequestCommand command);
}
