package com.aipaca.rumbo.platform.routes.application.commandservices;
import com.aipaca.rumbo.platform.routes.domain.model.Route;
import com.aipaca.rumbo.platform.routes.domain.model.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.commands.*;

public interface RouteCommandService {
    Route handle(CreateRouteCommand command);
    Route handle(UpdateRouteCommand command);
    Route handle(UpdateRouteScheduleCommand command);
    RouteAssignment handle(AssignStudentCommand command);
    Route handle(PublishRouteCommand command);
    Route handle(ReplaceRouteAssignmentsCommand command);
    void handle(DeleteRouteCommand command);
}
