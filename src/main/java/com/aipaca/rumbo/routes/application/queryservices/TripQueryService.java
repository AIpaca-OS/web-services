package com.aipaca.rumbo.routes.application.queryservices;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.model.queries.*;
import java.util.*;
public interface TripQueryService {
    Optional<Trip> handle(GetTripByIdQuery query);
    List<Trip> handle(GetTripsByRouteQuery query);
}
