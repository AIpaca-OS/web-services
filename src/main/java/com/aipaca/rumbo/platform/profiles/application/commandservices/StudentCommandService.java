package com.aipaca.rumbo.platform.profiles.application.commandservices;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RevokeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateStudentInformationCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface StudentCommandService {
    Result<Long,ApplicationError> handle(RegisterStudentCommand command);
    Result<Long,ApplicationError> handle(UpdateStudentInformationCommand command);
    Result<Long,ApplicationError> handle(AuthorizeTutorStudentRelationshipCommand command);
    Result<Long,ApplicationError> handle(RevokeTutorStudentRelationshipCommand command);
}
