package com.aipaca.rumbo.platform.routes.application.queryservices;
import com.aipaca.rumbo.platform.routes.domain.model.Trip;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetTripByIdQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetTripsByRouteQuery;

import java.util.*;
public interface TripQueryService {
    Optional<Trip> handle(GetTripByIdQuery query);
    List<Trip> handle(GetTripsByRouteQuery query);
}
