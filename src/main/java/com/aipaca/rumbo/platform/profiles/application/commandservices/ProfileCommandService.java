package com.aipaca.rumbo.platform.profiles.application.commandservices;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateProfileContactCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface ProfileCommandService {
    Result<Long,ApplicationError> handle(CreateProfileCommand command);
    Result<Long,ApplicationError> handle(UpdateProfileContactCommand command);
}
