package com.aipaca.rumbo.platform.trips.interfaces.rest.transform;

import com.aipaca.rumbo.platform.trips.domain.model.commands.CancelTripCommand;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.CancelTripResource;

/**
 * Assembler to convert a CancelTripResource to a CancelTripCommand.
 */
public class CancelTripCommandFromResourceAssembler {
    /**
     * Converts a CancelTripResource to a CancelTripCommand.
     *
     * @param tripId   The trip id taken from the path.
     * @param resource The {@link CancelTripResource} resource to convert.
     * @return The {@link CancelTripCommand} command.
     */
    public static CancelTripCommand toCommandFromResource(Long tripId, CancelTripResource resource) {
        return new CancelTripCommand(tripId, resource.reason());
    }
}
