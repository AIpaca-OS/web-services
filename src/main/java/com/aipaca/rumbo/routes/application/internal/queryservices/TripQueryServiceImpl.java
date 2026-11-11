package com.aipaca.rumbo.routes.application.internal.queryservices;
import com.aipaca.rumbo.routes.application.queryservices.TripQueryService;
import com.aipaca.rumbo.routes.domain.model.Trip;
import com.aipaca.rumbo.routes.domain.model.queries.*;
import com.aipaca.rumbo.routes.domain.repositories.TripRepository;
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
