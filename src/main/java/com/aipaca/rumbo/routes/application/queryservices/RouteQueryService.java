package com.aipaca.rumbo.routes.application.queryservices;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.model.queries.*;
import java.util.*;
public interface RouteQueryService {
    List<Route> handle(GetAllRoutesQuery query);
    Optional<Route> handle(GetRouteByIdQuery query);
    List<RouteAssignment> handle(GetRouteAssignmentsQuery query);
}
