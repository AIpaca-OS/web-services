package com.aipaca.rumbo.platform.routes.application.internal.queryservices;
import com.aipaca.rumbo.platform.routes.application.queryservices.TripQueryService;
import com.aipaca.rumbo.platform.routes.domain.model.Trip;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetTripByIdQuery;
import com.aipaca.rumbo.platform.routes.domain.model.queries.GetTripsByRouteQuery;
import com.aipaca.rumbo.platform.routes.domain.repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Transactional(readOnly = true)
public class TripQueryServiceImpl implements TripQueryService {
    private final TripRepository repository;
    public TripQueryServiceImpl(TripRepository repository) { this.repository = repository; }
    @Override public Optional<Trip> handle(GetTripByIdQuery query) { return repository.findById(query.tripId()); }
    @Override public List<Trip> handle(GetTripsByRouteQuery query) {return repository.findByRouteIdOrderByServiceDateDesc(query.routeId());}
}
