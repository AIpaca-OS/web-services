package com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.entities.TripPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data repository for trip persistence entities.
 */
@Repository
public interface TripPersistenceRepository extends JpaRepository<TripPersistenceEntity, Long> {
    List<TripPersistenceEntity> findAllByRouteIdOrderByServiceDateDesc(RouteId routeId);

    List<TripPersistenceEntity> findAllByDriverProfileIdOrderByServiceDateDesc(DriverProfileId driverProfileId);

    boolean existsByRouteIdAndServiceDate(RouteId routeId, LocalDate serviceDate);
}
