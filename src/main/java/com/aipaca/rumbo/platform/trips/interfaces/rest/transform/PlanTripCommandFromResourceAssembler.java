package com.aipaca.rumbo.platform.trips.interfaces.rest.transform;

import com.aipaca.rumbo.platform.trips.domain.model.commands.PlanTripCommand;
import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.SchoolId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.PlanTripResource;

/**
 * Assembler to convert a PlanTripResource to a PlanTripCommand.
 */
public class PlanTripCommandFromResourceAssembler {
    /**
     * Converts a PlanTripResource to a PlanTripCommand.
     *
     * @param resource The {@link PlanTripResource} resource to convert.
     * @return The {@link PlanTripCommand} command.
     */
    public static PlanTripCommand toCommandFromResource(PlanTripResource resource) {
        var passengers = resource.passengers().stream()
                .map(passenger -> new TripPassenger(new StudentId(passenger.studentId()), passenger.stopId(),
                        passenger.stopPosition(), passenger.stopAddress(),
                        passenger.schoolId() == null ? null : new SchoolId(passenger.schoolId())))
                .toList();
        return new PlanTripCommand(resource.routeId(), resource.driverProfileId(), resource.vehicleId(),
                resource.date(), resource.shift(), passengers);
    }
}
