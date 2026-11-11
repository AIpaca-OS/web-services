package com.aipaca.rumbo.platform.routes.application.queryservices;
import com.aipaca.rumbo.platform.routes.domain.model.Route;
import com.aipaca.rumbo.platform.routes.domain.model.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetAllRoutesQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetRouteAssignmentsQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetRouteByIdQuery;

import java.util.*;
public interface RouteQueryService {
    List<Route> handle(GetAllRoutesQuery query);
    Optional<Route> handle(GetRouteByIdQuery query);
    List<RouteAssignment> handle(GetRouteAssignmentsQuery query);
}
