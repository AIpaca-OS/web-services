package com.aipaca.rumbo.platform.routes.application.commandservices;

import com.aipaca.rumbo.platform.routes.domain.model.commands.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Route Command Service
 */
public interface RouteCommandService {
    /**
     * Handle Create Route Command
     *
     * @param command The {@link CreateRouteCommand} Command
     * @return A {@link Result} containing the created route id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(CreateRouteCommand command);

    /**
     * Handle Update Route Command
     *
     * @param command The {@link UpdateRouteCommand} Command
     * @return A {@link Result} containing the updated route id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(UpdateRouteCommand command);

    /**
     * Handle Update Route Schedule Command
     *
     * @param command The {@link UpdateRouteScheduleCommand} Command
     * @return A {@link Result} containing the route id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(UpdateRouteScheduleCommand command);

    /**
     * Handle Assign Student To Route Command
     *
     * @param command The {@link AssignStudentToRouteCommand} Command
     * @return A {@link Result} containing the created assignment id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(AssignStudentToRouteCommand command);

    /**
     * Handle Replace Route Assignments Command
     *
     * @param command The {@link ReplaceRouteAssignmentsCommand} Command
     * @return A {@link Result} containing the route id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(ReplaceRouteAssignmentsCommand command);

    /**
     * Handle Publish Route Command
     *
     * @param command The {@link PublishRouteCommand} Command
     * @return A {@link Result} containing the published route id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(PublishRouteCommand command);

    /**
     * Handle Delete Route Command
     *
     * @param command The {@link DeleteRouteCommand} Command
     * @return A {@link Result} containing the deleted route id, or an {@link ApplicationError}
     */
    Result<Long, ApplicationError> handle(DeleteRouteCommand command);
}
