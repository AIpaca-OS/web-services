package com.aipaca.rumbo.platform.trips.application.queryservices;

import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetAllTripsByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetAllTripsByRouteIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetTripByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Trip Query Service
 */
public interface TripQueryService {
    /**
     * Handle Get Trip By ID Query
     *
     * @param query The {@link GetTripByIdQuery} Query
     * @return A {@link Trip} instance if found, otherwise empty
     */
    Optional<Trip> handle(GetTripByIdQuery query);

    /**
     * Handle Get All Trips By Route ID Query
     *
     * @param query The {@link GetAllTripsByRouteIdQuery} Query
     * @return The trips of the route, most recent service date first
     */
    List<Trip> handle(GetAllTripsByRouteIdQuery query);

    /**
     * Handle Get All Trips By Driver Profile ID Query
     *
     * @param query The {@link GetAllTripsByDriverProfileIdQuery} Query
     * @return The trips of the driver, most recent service date first
     */
    List<Trip> handle(GetAllTripsByDriverProfileIdQuery query);
}
