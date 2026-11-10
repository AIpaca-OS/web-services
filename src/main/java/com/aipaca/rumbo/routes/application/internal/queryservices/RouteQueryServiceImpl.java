package com.aipaca.rumbo.routes.application.internal.queryservices;
import com.aipaca.rumbo.routes.application.queryservices.RouteQueryService;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.model.queries.*;
import com.aipaca.rumbo.routes.domain.repositories.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Transactional(readOnly = true)
public class RouteQueryServiceImpl implements RouteQueryService {
    private final RouteRepository repository;
    public RouteQueryServiceImpl(RouteRepository repository) { this.repository = repository; }
    @Override public List<Route> handle(GetAllRoutesQuery query) { return repository.findAll(); }
    @Override public Optional<Route> handle(GetRouteByIdQuery query) { return repository.findById(query.routeId()); }
    @Override public List<RouteAssignment> handle(GetRouteAssignmentsQuery query) {
        return repository.findById(query.routeId()).map(Route::getAssignments).orElse(List.of());
    }
}
