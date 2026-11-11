package com.aipaca.rumbo.routes.application.internal.commandservices;
import com.aipaca.rumbo.routes.application.commandservices.RouteCommandService;
import com.aipaca.rumbo.routes.application.ports.StudentAuthorizationPort;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.events.RoutePublishedEvent;
import com.aipaca.rumbo.routes.domain.model.commands.*;
import com.aipaca.rumbo.routes.domain.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@Service @Transactional
public class RouteCommandServiceImpl implements RouteCommandService {
    private final RouteRepository repository;
    private final TripRepository trips;
    private final StudentAuthorizationPort studentAuthorization;
    private final ApplicationEventPublisher events;

    /** Backwards-compatible constructor for plain service tests. */
    public RouteCommandServiceImpl(RouteRepository repository, TripRepository trips,
                                   StudentAuthorizationPort studentAuthorization) {
        this(repository, trips, studentAuthorization, event -> {});
    }

    @Autowired
    public RouteCommandServiceImpl(RouteRepository repository, TripRepository trips,
                                   StudentAuthorizationPort studentAuthorization,
                                   ApplicationEventPublisher events) {
        this.repository=repository; this.trips=trips;
        this.studentAuthorization=studentAuthorization; this.events=events;
    }
    private Route required(UUID id) {
        return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Route not found"));
    }
    private void requireConfigurable(Route route) {
        if (route.getStatus() != RouteStatus.CONFIGURABLE)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Published routes cannot be reconfigured");
    }
    private void authorizePublishedEdit(Route route, String authorizationHeader) {
        if (route.getStatus() == RouteStatus.PUBLISHED &&
                !studentAuthorization.canManageVehicle(route.getVehicleId(), authorizationHeader))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Driver cannot modify this published route");
    }
    private void verifyPublishedAssignments(Route route, String authorizationHeader) {
        route.validateForPublication();
        for (RouteAssignment assignment : route.getAssignments()) {
            if (!studentAuthorization.isAuthorized(
                    assignment.getStudentId(), route.getVehicleId(), authorizationHeader))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "An assigned student is no longer authorized");
        }
    }
    @Override public Route handle(CreateRouteCommand command) {
        Route route=new Route(command.name(),command.shift(),command.vehicleId());
        route.replaceStops(command.stops());
        route.updateSchedule(command.schedule(),command.scheduleConfirmed());
        return repository.save(route);
    }
    @Override public Route handle(UpdateRouteCommand command) {
        Route route=required(command.routeId());
        boolean published=route.getStatus()==RouteStatus.PUBLISHED;
        authorizePublishedEdit(route,command.authorizationHeader());
        if (published && !Objects.equals(route.getVehicleId(),command.vehicleId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A published route cannot change vehicle");
        route.rename(command.name());
        route.setShift(command.shift());
        route.setVehicleId(command.vehicleId());
        route.replaceStops(command.stops());
        route.updateSchedule(command.schedule(),command.scheduleConfirmed());
        if (published) verifyPublishedAssignments(route,command.authorizationHeader());
        return repository.save(route);
    }
    @Override public Route handle(UpdateRouteScheduleCommand command) {
        Route route=required(command.routeId());
        boolean published=route.getStatus()==RouteStatus.PUBLISHED;
        authorizePublishedEdit(route,command.authorizationHeader());
        route.updateSchedule(command.schedule(),command.confirm());
        if (published) verifyPublishedAssignments(route,command.authorizationHeader());
        return repository.save(route);
    }
    @Override public RouteAssignment handle(AssignStudentCommand command) {
        Route route=required(command.routeId());
        requireConfigurable(route);
        if (!studentAuthorization.isAuthorized(command.studentId(), route.getVehicleId(), command.authorizationHeader()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Student is not authorized for this driver");
        if (route.getAssignments().stream().anyMatch(a -> a.getStudentId().equals(command.studentId())))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Student is already assigned to this route");
        if (route.getStops().stream().noneMatch(s -> s.getId().equals(command.stopId())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Stop is not part of this route");
        RouteAssignment assignment=route.assign(command.stopId(),command.studentId());
        repository.save(route);
        return assignment;
    }
    @Override public Route handle(PublishRouteCommand command) {
        Route route=required(command.routeId());
        // Check route completeness before asking Profiles about student permissions.
        route.validateForPublication();
        for (RouteAssignment assignment : route.getAssignments()) {
            if (!studentAuthorization.isAuthorized(assignment.getStudentId(), route.getVehicleId(), command.authorizationHeader()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"A route student is no longer authorized");
        }
        route.publish();
        Route saved = repository.save(route);
        events.publishEvent(new RoutePublishedEvent(saved.getId()));
        return saved;
    }
    @Override public Route handle(ReplaceRouteAssignmentsCommand command) {
        Route route = required(command.routeId());
        requireConfigurable(route);
        if (!studentAuthorization.canManageVehicle(route.getVehicleId(), command.authorizationHeader()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Driver cannot manage this vehicle");
        if (command.assignments() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignments are required");

        Set<String> studentIds = new HashSet<>();
        Set<UUID> stopIds = new HashSet<>();
        for (RouteStop stop : route.getStops()) stopIds.add(stop.getId());

        for (RouteAssignment assignment : command.assignments()) {
            if (!stopIds.contains(assignment.getStopId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stop is not part of this route");
            if (!studentIds.add(assignment.getStudentId()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Student is assigned more than once");
            if (!studentAuthorization.isAuthorized(assignment.getStudentId(), route.getVehicleId(), command.authorizationHeader()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student is not authorized for this driver");
        }
        route.replaceAssignments(command.assignments());
        return repository.save(route);
    }
    @Override public void handle(DeleteRouteCommand command) {
        Route route=required(command.routeId());
        if (trips.existsByRouteId(command.routeId())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Route has trips");
        repository.delete(route);
    }
}
