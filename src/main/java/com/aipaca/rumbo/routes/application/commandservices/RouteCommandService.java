package com.aipaca.rumbo.routes.application.commandservices;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.model.commands.*;
public interface RouteCommandService {
    Route handle(CreateRouteCommand command);
    Route handle(UpdateRouteCommand command);
    Route handle(UpdateRouteScheduleCommand command);
    RouteAssignment handle(AssignStudentCommand command);
    Route handle(PublishRouteCommand command);
    void handle(DeleteRouteCommand command);
}
