package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteAssignment;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteAssignmentResource;

/**
 * Assembler to convert a RouteAssignment entity to a RouteAssignmentResource.
 */
public class RouteAssignmentResourceFromEntityAssembler {
    /**
     * Converts a RouteAssignment entity to a RouteAssignmentResource.
     *
     * @param entity The {@link RouteAssignment} entity to convert.
     * @return The {@link RouteAssignmentResource} resource.
     */
    public static RouteAssignmentResource toResourceFromEntity(RouteAssignment entity) {
        return new RouteAssignmentResource(entity.getId(), entity.getStopId(), entity.getStudentIdValue());
    }
}
