package com.aipaca.rumbo.incidents.domain.repositories;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;

import java.util.List;
import java.util.Optional;

/**
 * Delay repository port.
 */
public interface DelayRepository {
    Optional<Delay> findById(Long id);

    List<Delay> findAllByTripId(TripId tripId);

    Delay save(Delay delay);
}
