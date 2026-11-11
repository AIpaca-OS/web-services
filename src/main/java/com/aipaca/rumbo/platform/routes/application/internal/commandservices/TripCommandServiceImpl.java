package com.aipaca.rumbo.platform.routes.application.internal.commandservices;

import com.aipaca.rumbo.platform.routes.application.commandservices.TripCommandService;
import com.aipaca.rumbo.platform.routes.domain.events.TripProgressEvent;
import com.aipaca.rumbo.platform.routes.domain.model.Route;
import com.aipaca.rumbo.platform.routes.domain.model.Trip;
import com.aipaca.rumbo.platform.routes.domain.model.TripPassenger;
import com.aipaca.rumbo.platform.routes.domain.model.commands.*;
import com.aipaca.rumbo.platform.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.platform.routes.domain.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TripCommandServiceImpl implements TripCommandService {
    private final RouteRepository routes;
    private final TripRepository trips;
    private final ApplicationEventPublisher events;

    /** Preserve the constructor used by existing unit tests. */
    public TripCommandServiceImpl(RouteRepository routes, TripRepository trips) {
        this(routes, trips, null);
    }
    @Autowired
    public TripCommandServiceImpl(RouteRepository routes, TripRepository trips,
                                  ApplicationEventPublisher events) {
        this.routes = routes;
        this.trips = trips;
        this.events = events;
    }

    private Trip required(UUID id) {
        return trips.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
    }
    private void notifyProgress(Trip trip, String kind, List<String> students) {
        if (events != null && !students.isEmpty()) {
            events.publishEvent(new TripProgressEvent(
                    trip.getRouteId(), trip.getId(), kind, students));
        }
    }
    private List<String> allStudents(Trip trip) {
        return trip.getPassengers().stream().map(TripPassenger::getStudentId).distinct().toList();
    }

    @Override
    public Trip handle(PlanTripCommand command) {
        Route route = routes.findById(command.routeId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found"));
        if (trips.existsByRouteIdAndServiceDate(command.routeId(), command.date())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Trip already exists for date");
        }
        Trip trip = trips.save(new Trip(route, command.date()));
        notifyProgress(trip, "TRIP_PLANNED", allStudents(trip));
        return trip;
    }
    @Override
    public Trip handle(MarkAbsenceCommand command) {
        Trip trip = required(command.tripId());
        trip.markAbsent(command.studentId());
        Trip result = trips.save(trip);
        notifyProgress(result, "ABSENT", List.of(command.studentId()));
        return result;
    }
    @Override
    public Trip handle(StartTripCommand command) {
        Trip trip = required(command.tripId());
        trip.start();
        Trip result = trips.save(trip);
        notifyProgress(result, "TRIP_STARTED", allStudents(result));
        return result;
    }
    @Override
    public Trip handle(ServeStopCommand command) {
        Trip trip = required(command.tripId());
        List<String> affected = trip.getPassengers().stream()
                .filter(p -> command.stopId().equals(p.getStopId()) && !p.isAbsent())
                .map(TripPassenger::getStudentId).distinct().toList();
        trip.serveStop(command.stopId());
        Trip result = trips.save(trip);
        notifyProgress(result, "STOP_SERVED", affected);
        return result;
    }
    @Override
    public Trip handle(CancelTripCommand command) {
        Trip trip = required(command.tripId());
        trip.cancel(command.reason());
        Trip result = trips.save(trip);
        notifyProgress(result, "TRIP_CANCELLED", allStudents(result));
        return result;
    }
    @Override
    public Trip handle(ConfirmStudentArrivalCommand command) {
        Trip trip = required(command.tripId());
        trip.confirmStudentArrival(command.studentId());
        Trip saved = trips.save(trip);
        notifyProgress(saved, "STUDENT_ARRIVED", List.of(command.studentId()));
        return saved;
    }
    @Override
    public Trip handle(CompleteTripCommand command) {
        Trip trip = required(command.tripId());
        trip.complete();
        Trip result = trips.save(trip);
        List<String> presentStudents = result.getPassengers().stream()
                .filter(p -> !p.isAbsent()).map(TripPassenger::getStudentId).distinct().toList();
        notifyProgress(result, "TRIP_COMPLETED", presentStudents);
        return result;
    }
}
