package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.commands.AssignStudentToRouteCommand;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.AssignStudentResource;

/**
 * Assembler to convert an AssignStudentResource to an AssignStudentToRouteCommand.
 */
public class AssignStudentToRouteCommandFromResourceAssembler {
    /**
     * Converts an AssignStudentResource to an AssignStudentToRouteCommand.
     *
     * @param routeId  The route id taken from the path.
     * @param resource The {@link AssignStudentResource} resource to convert.
     * @return The {@link AssignStudentToRouteCommand} command.
     */
    public static AssignStudentToRouteCommand toCommandFromResource(Long routeId, AssignStudentResource resource) {
        return new AssignStudentToRouteCommand(routeId, resource.stopId(), resource.studentId());
    }
}
