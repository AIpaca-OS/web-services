package com.aipaca.rumbo.platform.trips.domain.repositories;

import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Trip repository port.
 */
public interface TripRepository {
    Optional<Trip> findById(Long id);

    List<Trip> findAllByRouteId(RouteId routeId);

    List<Trip> findAllByDriverProfileId(DriverProfileId driverProfileId);

    Trip save(Trip trip);

    boolean existsByRouteIdAndServiceDate(RouteId routeId, LocalDate serviceDate);
}
