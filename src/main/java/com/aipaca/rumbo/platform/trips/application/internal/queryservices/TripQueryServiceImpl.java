package com.aipaca.rumbo.platform.trips.application.internal.queryservices;

import com.aipaca.rumbo.platform.trips.application.queryservices.TripQueryService;
import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetAllTripsByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetAllTripsByRouteIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetTripByIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.trips.domain.repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Trip Query Service Implementation
 */
@Service("platformTripsQueryService")
@Transactional(readOnly = true)
public class TripQueryServiceImpl implements TripQueryService {
    private final TripRepository tripRepository;

    /**
     * Constructor
     *
     * @param tripRepository The {@link TripRepository} instance
     */
    public TripQueryServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Trip> handle(GetTripByIdQuery query) {
        return tripRepository.findById(query.tripId());
    }

    // inherited javadoc
    @Override
    public List<Trip> handle(GetAllTripsByRouteIdQuery query) {
        return tripRepository.findAllByRouteId(new RouteId(query.routeId()));
    }

    // inherited javadoc
    @Override
    public List<Trip> handle(GetAllTripsByDriverProfileIdQuery query) {
        return tripRepository.findAllByDriverProfileId(new DriverProfileId(query.driverProfileId()));
    }
}
