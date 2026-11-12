package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.assemblers.RoutePersistenceAssembler;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.repositories.RoutePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain route repository port with Spring Data JPA.
 */
@Repository
public class RouteRepositoryImpl implements RouteRepository {

    private final RoutePersistenceRepository routePersistenceRepository;

    public RouteRepositoryImpl(RoutePersistenceRepository routePersistenceRepository) {
        this.routePersistenceRepository = routePersistenceRepository;
    }

    @Override
    public Optional<Route> findById(Long id) {
        return routePersistenceRepository.findById(id).map(RoutePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Route> findAll() {
        return routePersistenceRepository.findAll().stream().map(RoutePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Route> findAllByDriverProfileId(DriverProfileId driverProfileId) {
        return routePersistenceRepository.findAllByDriverProfileId(driverProfileId).stream()
                .map(RoutePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Route save(Route route) {
        var savedEntity = routePersistenceRepository.save(RoutePersistenceAssembler.toPersistenceFromDomain(route));
        return RoutePersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public boolean existsById(Long id) {
        return routePersistenceRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        routePersistenceRepository.deleteById(id);
    }
}
