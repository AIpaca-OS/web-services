package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateProfileContactCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Profile Command Service
 */
public interface ProfileCommandService {
    /**
     * Handle Create Profile Command
     *
     * @param command The {@link CreateProfileCommand} Command
     * @return A {@link Result} containing the id of the created profile on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(CreateProfileCommand command);

    /**
     * Handle Update Profile Contact Command
     *
     * @param command The {@link UpdateProfileContactCommand} Command
     * @return A {@link Result} containing the id of the updated profile on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(UpdateProfileContactCommand command);
}
