package com.aipaca.rumbo.routes.application.internal.commandservices;
import com.aipaca.rumbo.routes.application.commandservices.TripCommandService;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.model.commands.*;
import com.aipaca.rumbo.routes.domain.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@Service @Transactional
public class TripCommandServiceImpl implements TripCommandService {
    private final RouteRepository routes;
    private final TripRepository trips;
    public TripCommandServiceImpl(RouteRepository routes,TripRepository trips){this.routes=routes;this.trips=trips;}
    private Trip required(UUID id) {return trips.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Trip not found"));}
    @Override public Trip handle(PlanTripCommand command) {
        Route route=routes.findById(command.routeId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Route not found"));
        if (trips.existsByRouteIdAndServiceDate(command.routeId(),command.date()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Trip already exists for date");
        return trips.save(new Trip(route,command.date()));
    }
    @Override public Trip handle(MarkAbsenceCommand command) {Trip t=required(command.tripId());t.markAbsent(command.studentId());return trips.save(t);}
    @Override public Trip handle(StartTripCommand command) {Trip t=required(command.tripId());t.start();return trips.save(t);}
    @Override public Trip handle(ServeStopCommand command) {Trip t=required(command.tripId());t.serveStop(command.stopId());return trips.save(t);}
    @Override public Trip handle(CancelTripCommand command) {Trip t=required(command.tripId());t.cancel(command.reason());return trips.save(t);}
    @Override public Trip handle(CompleteTripCommand command) {Trip t=required(command.tripId());t.complete();return trips.save(t);}
}
