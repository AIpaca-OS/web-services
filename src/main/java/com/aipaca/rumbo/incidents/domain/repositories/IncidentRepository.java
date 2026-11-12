package com.aipaca.rumbo.incidents.domain.repositories;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;

import java.util.List;
import java.util.Optional;

/**
 * Incident repository port.
 */
public interface IncidentRepository {
    Optional<Incident> findById(Long id);

    List<Incident> findAllByTripId(TripId tripId);

    Incident save(Incident incident);
}
