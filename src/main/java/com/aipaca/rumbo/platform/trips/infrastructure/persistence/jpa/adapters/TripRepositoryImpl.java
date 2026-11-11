package com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.trips.domain.repositories.TripRepository;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.assemblers.TripPersistenceAssembler;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.repositories.TripPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain trip repository port with Spring Data JPA.
 */
@Repository
public class TripRepositoryImpl implements TripRepository {

    private final TripPersistenceRepository tripPersistenceRepository;

    public TripRepositoryImpl(TripPersistenceRepository tripPersistenceRepository) {
        this.tripPersistenceRepository = tripPersistenceRepository;
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return tripPersistenceRepository.findById(id).map(TripPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Trip> findAllByRouteId(RouteId routeId) {
        return tripPersistenceRepository.findAllByRouteIdOrderByServiceDateDesc(routeId).stream()
                .map(TripPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Trip> findAllByDriverProfileId(DriverProfileId driverProfileId) {
        return tripPersistenceRepository.findAllByDriverProfileIdOrderByServiceDateDesc(driverProfileId).stream()
                .map(TripPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Trip save(Trip trip) {
        var savedEntity = tripPersistenceRepository.save(TripPersistenceAssembler.toPersistenceFromDomain(trip));
        return TripPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public boolean existsByRouteIdAndServiceDate(RouteId routeId, LocalDate serviceDate) {
        return tripPersistenceRepository.existsByRouteIdAndServiceDate(routeId, serviceDate);
    }
}
