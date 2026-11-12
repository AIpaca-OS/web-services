package com.aipaca.rumbo.platform.trips.interfaces.rest.transform;

import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.TripPassengerResource;
import com.aipaca.rumbo.platform.trips.interfaces.rest.resources.TripResource;

/**
 * Assembler to convert a Trip entity to a TripResource.
 */
public class TripResourceFromEntityAssembler {
    /**
     * Converts a Trip entity to a TripResource.
     *
     * @param entity The {@link Trip} entity to convert.
     * @return The {@link TripResource} resource.
     */
    public static TripResource toResourceFromEntity(Trip entity) {
        var passengers = entity.getPassengers().stream()
                .map(passenger -> new TripPassengerResource(passenger.getStudentIdValue(), passenger.getStopId(),
                        passenger.getStopPosition(), passenger.getStopAddress(), passenger.isAbsent(),
                        passenger.isStopServed(), passenger.isArrived()))
                .toList();
        return new TripResource(entity.getId(), entity.getRouteIdValue(), entity.getDriverProfileIdValue(),
                entity.getVehicleIdValue(), entity.getServiceDate(),
                entity.getShift(), entity.getStatus(), entity.getCancelReason(), passengers);
    }
}
