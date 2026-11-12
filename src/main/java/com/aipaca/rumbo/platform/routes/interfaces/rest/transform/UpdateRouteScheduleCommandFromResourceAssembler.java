package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.commands.UpdateRouteScheduleCommand;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.UpdateRouteScheduleResource;

/**
 * Assembler to convert an UpdateRouteScheduleResource to an UpdateRouteScheduleCommand.
 */
public class UpdateRouteScheduleCommandFromResourceAssembler {
    /**
     * Converts an UpdateRouteScheduleResource to an UpdateRouteScheduleCommand.
     *
     * @param routeId  The route id taken from the path.
     * @param resource The {@link UpdateRouteScheduleResource} resource to convert.
     * @return The {@link UpdateRouteScheduleCommand} command.
     */
    public static UpdateRouteScheduleCommand toCommandFromResource(Long routeId, UpdateRouteScheduleResource resource) {
        return new UpdateRouteScheduleCommand(
                routeId,
                RouteScheduleFromResourceAssembler.toValueObjectsFromResources(resource.schedule()),
                resource.confirm());
    }
}
