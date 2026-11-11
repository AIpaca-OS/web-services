package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.commands.UpdateRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.SchoolId;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.UpdateRouteResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler to convert an UpdateRouteResource to an UpdateRouteCommand.
 */
public class UpdateRouteCommandFromResourceAssembler {
    /**
     * Converts an UpdateRouteResource to an UpdateRouteCommand.
     *
     * @param routeId  The route id taken from the path.
     * @param resource The {@link UpdateRouteResource} resource to convert.
     * @return The {@link UpdateRouteCommand} command.
     */
    public static UpdateRouteCommand toCommandFromResource(Long routeId, UpdateRouteResource resource) {
        List<RouteStop> stops = new ArrayList<>();
        if (resource.stops() != null) {
            for (int i = 0; i < resource.stops().size(); i++) {
                var stop = resource.stops().get(i);
                stops.add(new RouteStop(stop.id(), stop.address(),
                        stop.schoolId() == null ? null : new SchoolId(stop.schoolId()), i + 1));
            }
        }
        return new UpdateRouteCommand(
                routeId,
                resource.name(),
                resource.shift(),
                resource.vehicleId(),
                stops,
                RouteScheduleFromResourceAssembler.toValueObjectsFromResources(resource.schedule()),
                Boolean.TRUE.equals(resource.scheduleConfirmed()));
    }
}
