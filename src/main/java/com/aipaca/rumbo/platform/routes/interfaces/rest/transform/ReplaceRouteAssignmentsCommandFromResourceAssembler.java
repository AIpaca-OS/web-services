package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.commands.ReplaceRouteAssignmentsCommand;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.AssignStudentResource;

import java.util.List;

/**
 * Assembler to convert a list of AssignStudentResource to a ReplaceRouteAssignmentsCommand.
 */
public class ReplaceRouteAssignmentsCommandFromResourceAssembler {
    /**
     * Converts the complete list of assignments to a ReplaceRouteAssignmentsCommand.
     *
     * @param routeId   The route id taken from the path.
     * @param resources The {@link AssignStudentResource} resources to convert.
     * @return The {@link ReplaceRouteAssignmentsCommand} command.
     */
    public static ReplaceRouteAssignmentsCommand toCommandFromResources(Long routeId, List<AssignStudentResource> resources) {
        var assignments = resources.stream()
                .map(resource -> new RouteAssignment(resource.stopId(), new StudentId(resource.studentId())))
                .toList();
        return new ReplaceRouteAssignmentsCommand(routeId, assignments);
    }
}
