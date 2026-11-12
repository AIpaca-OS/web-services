package com.aipaca.rumbo.platform.routes.application.queryservices;

import com.aipaca.rumbo.platform.routes.domain.model.aggregates.Route;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetAllRoutesByDriverProfileIdQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetAllRoutesQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetRouteByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Route Query Service
 */
public interface RouteQueryService {
    /**
     * Handle Get Route By ID Query
     *
     * @param query The {@link GetRouteByIdQuery} Query
     * @return A {@link Route} instance if found, otherwise empty
     */
    Optional<Route> handle(GetRouteByIdQuery query);

    /**
     * Handle Get All Routes Query
     *
     * @param query The {@link GetAllRoutesQuery} Query
     * @return A list of {@link Route} instances
     */
    List<Route> handle(GetAllRoutesQuery query);

    /**
     * Handle Get All Routes By Driver Profile ID Query
     *
     * @param query The {@link GetAllRoutesByDriverProfileIdQuery} Query
     * @return A list of {@link Route} instances of the driver
     */
    List<Route> handle(GetAllRoutesByDriverProfileIdQuery query);
}
