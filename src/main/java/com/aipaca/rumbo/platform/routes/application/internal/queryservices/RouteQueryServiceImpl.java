package com.aipaca.rumbo.platform.routes.application.internal.queryservices;

import com.aipaca.rumbo.platform.routes.application.queryservices.RouteQueryService;
import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetAllRoutesByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetAllRoutesQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetRouteByIdQuery;
import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.routes.domain.repositories.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Route Query Service Implementation
 */
@Service("platformRoutesQueryService")
@Transactional(readOnly = true)
public class RouteQueryServiceImpl implements RouteQueryService {
    private final RouteRepository routeRepository;

    /**
     * Constructor
     *
     * @param routeRepository The {@link RouteRepository} instance
     */
    public RouteQueryServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Route> handle(GetRouteByIdQuery query) {
        return routeRepository.findById(query.routeId());
    }

    // inherited javadoc
    @Override
    public List<Route> handle(GetAllRoutesQuery query) {
        return routeRepository.findAll();
    }

    // inherited javadoc
    @Override
    public List<Route> handle(GetAllRoutesByDriverProfileIdQuery query) {
        return routeRepository.findAllByDriverProfileId(new DriverProfileId(query.driverProfileId()));
    }
}
