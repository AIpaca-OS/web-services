package com.aipaca.rumbo.platform.trips.infrastructure.acl;

import com.aipaca.rumbo.platform.shared.application.acl.TripInformationPort;
import com.aipaca.rumbo.platform.trips.application.queryservices.TripQueryService;
import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import com.aipaca.rumbo.platform.trips.domain.model.queries.GetTripByIdQuery;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.TripStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adapts Trips' own query service to the shared read-only contract. */
@Component
public class TripInformationAdapter implements TripInformationPort {
    private final TripQueryService trips;

    public TripInformationAdapter(TripQueryService trips) {
        this.trips = trips;
    }

    @Override
    public Optional<TripSummary> findById(Long tripId) {
        return trips.handle(new GetTripByIdQuery(tripId))
                .map(trip -> new TripSummary(
                        trip.getRouteIdValue(),
                        trip.getDriverProfileIdValue(),
                        trip.getStatus() == TripStatus.IN_PROGRESS,
                        trip.getPassengers().stream()
                                .map(TripPassenger::getStudentIdValue).distinct().toList()));
    }
}
