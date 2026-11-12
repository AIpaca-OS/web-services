package com.aipaca.rumbo.platform.profiles.application.commandservices;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RevokeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateStudentInformationCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Student Command Service
 */
public interface StudentCommandService {
    /**
     * Handle Register Student Command
     *
     * @param command The {@link RegisterStudentCommand} Command
     * @return A {@link Result} containing the id of the registered student on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(RegisterStudentCommand command);

    /**
     * Handle Update Student Information Command
     *
     * @param command The {@link UpdateStudentInformationCommand} Command
     * @return A {@link Result} containing the id of the updated student on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(UpdateStudentInformationCommand command);

    /**
     * Handle Authorize Tutor Student Relationship Command
     *
     * @param command The {@link AuthorizeTutorStudentRelationshipCommand} Command
     * @return A {@link Result} containing the id of the student on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(AuthorizeTutorStudentRelationshipCommand command);

    /**
     * Handle Revoke Tutor Student Relationship Command
     *
     * @param command The {@link RevokeTutorStudentRelationshipCommand} Command
     * @return A {@link Result} containing the id of the student on success, or an {@link ApplicationError} on failure
     */
    Result<Long, ApplicationError> handle(RevokeTutorStudentRelationshipCommand command);
}
