package com.aipaca.rumbo.platform.trips.domain.model.commands;

import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteShift;

import java.time.LocalDate;
import java.util.List;

/**
 * Plan Trip Command
 *
 * <p>Carries the snapshot of the published route the trip is planned from:
 * its id, its driver, its vehicle, its shift and the students assigned to each stop.</p>
 *
 * @param routeId         id of the published route
 * @param driverProfileId id of the driver profile that runs the trip
 * @param vehicleId       id of the vehicle that serves the trip
 * @param serviceDate service date
 * @param shift       route shift
 * @param passengers  assigned students with their stop
 */
public record PlanTripCommand(Long routeId, Long driverProfileId, Long vehicleId, LocalDate serviceDate,
                              RouteShift shift, List<TripPassenger> passengers) {
    public PlanTripCommand {
        if (routeId == null || routeId < 1) {
            throw new IllegalArgumentException("Route id cannot be null or less than 1");
        }
        if (driverProfileId == null || driverProfileId < 1) {
            throw new IllegalArgumentException("Driver profile id cannot be null or less than 1");
        }
        if (vehicleId == null || vehicleId < 1) {
            throw new IllegalArgumentException("Vehicle id cannot be null or less than 1");
        }
        if (serviceDate == null) {
            throw new IllegalArgumentException("Service date cannot be null");
        }
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }
        passengers = passengers == null ? List.of() : List.copyOf(passengers);
    }
}
