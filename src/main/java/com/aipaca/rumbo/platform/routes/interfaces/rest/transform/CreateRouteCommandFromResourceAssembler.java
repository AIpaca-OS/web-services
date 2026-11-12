package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.commands.CreateRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.entities.RouteStop;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.CreateRouteResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler to convert a CreateRouteResource to a CreateRouteCommand.
 */
public class CreateRouteCommandFromResourceAssembler {
    /**
     * Converts a CreateRouteResource to a CreateRouteCommand.
     *
     * @param resource The {@link CreateRouteResource} resource to convert.
     * @return The {@link CreateRouteCommand} command.
     */
    public static CreateRouteCommand toCommandFromResource(CreateRouteResource resource) {
        List<RouteStop> stops = new ArrayList<>();
        if (resource.stops() != null) {
            for (int i = 0; i < resource.stops().size(); i++) {
                var stop = resource.stops().get(i);
                stops.add(new RouteStop(null, stop.address(), i + 1));
            }
        }
        return new CreateRouteCommand(
                resource.driverProfileId(),
                resource.name(),
                resource.shift(),
                resource.vehicleId(),
                stops,
                RouteScheduleFromResourceAssembler.toValueObjectsFromResources(resource.schedule()),
                Boolean.TRUE.equals(resource.scheduleConfirmed()));
    }
}
