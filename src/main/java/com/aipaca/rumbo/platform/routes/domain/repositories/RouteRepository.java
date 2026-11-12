package com.aipaca.rumbo.platform.routes.domain.repositories;

import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.DriverProfileId;

import java.util.List;
import java.util.Optional;

/**
 * Route repository port.
 */
public interface RouteRepository {
    Optional<Route> findById(Long id);

    List<Route> findAll();

    List<Route> findAllByDriverProfileId(DriverProfileId driverProfileId);

    Route save(Route route);

    boolean existsById(Long id);

    void deleteById(Long id);
}
