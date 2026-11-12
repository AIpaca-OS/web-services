package com.aipaca.rumbo.platform.trips.application.internal.commandservices;

import com.aipaca.rumbo.platform.trips.application.commandservices.TripCommandService;
import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.domain.model.commands.*;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.trips.domain.repositories.TripRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import com.aipaca.rumbo.platform.shared.application.events.StudentJourneyEvent;
import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Trip Command Service Implementation
 */
@Service("platformTripsCommandService")
@Transactional
public class TripCommandServiceImpl implements TripCommandService {
    private final TripRepository tripRepository;
    private final ApplicationEventPublisher events;

    public TripCommandServiceImpl(TripRepository tripRepository, ApplicationEventPublisher events) {
        this.tripRepository = tripRepository;
        this.events = events;
    }

    @Override
    public Result<Long, ApplicationError> handle(PlanTripCommand command) {
        if (tripRepository.existsByRouteIdAndServiceDate(new RouteId(command.routeId()), command.serviceDate()))
            return Result.failure(ApplicationError.conflict("Trip",
                    "A trip is already scheduled for %s".formatted(command.serviceDate())));
        try {
            var saved = tripRepository.save(new Trip(command));
            notifyStudents(saved, "TRIP_PLANNED", "Viaje programado", null, null);
            return Result.success(saved.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Trip", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("plan-trip", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(StartTripCommand command) {
        return transition(command.tripId(), "start-trip", Trip::start,
                "TRIP_STARTED", "Recorrido iniciado", null, null);
    }

    @Override
    public Result<Long, ApplicationError> handle(MarkStudentAbsenceCommand command) {
        return transition(command.tripId(), "mark-student-absence",
                trip -> trip.markStudentAbsent(new StudentId(command.studentId())),
                "ABSENT", "Ausencia registrada", command.studentId(), null);
    }

    @Override
    public Result<Long, ApplicationError> handle(ServeTripStopCommand command) {
        return transition(command.tripId(), "serve-trip-stop", trip -> trip.serveStop(command.stopId()),
                "STOP_SERVED", "Parada atendida", null, command.stopId());
    }

    @Override
    public Result<Long, ApplicationError> handle(ConfirmStudentArrivalCommand command) {
        return transition(command.tripId(), "confirm-student-arrival",
                trip -> trip.confirmStudentArrival(new StudentId(command.studentId())),
                "STUDENT_ARRIVED", "Llegada confirmada", command.studentId(), null);
    }

    @Override
    public Result<Long, ApplicationError> handle(CancelTripCommand command) {
        return transition(command.tripId(), "cancel-trip", trip -> trip.cancel(command.reason()),
                "TRIP_CANCELLED", "Viaje cancelado", null, null);
    }

    @Override
    public Result<Long, ApplicationError> handle(CompleteTripCommand command) {
        return transition(command.tripId(), "complete-trip", Trip::complete,
                "TRIP_COMPLETED", "Recorrido finalizado", null, null);
    }

    private Result<Long, ApplicationError> transition(
            Long tripId, String context, Consumer<Trip> change,
            String fact, String title, Long studentId, Long stopId) {
        var result = tripRepository.findById(tripId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Trip", tripId.toString()));
        var trip = result.get();
        try {
            change.accept(trip);
            var saved = tripRepository.save(trip);
            notifyStudents(saved, fact, title, studentId, stopId);
            return Result.success(saved.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation(context, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(context, e.getMessage()));
        }
    }

    private void notifyStudents(Trip trip, String fact, String title, Long studentId, Long stopId) {
        var students = trip.getPassengers().stream()
                .filter(p -> studentId == null || p.getStudentIdValue().equals(studentId))
                .filter(p -> stopId == null || p.getStopId().equals(stopId))
                .filter(p -> !"STOP_SERVED".equals(fact) || !p.isAbsent())
                .map(TripPassenger::getStudentIdValue).distinct().toList();
        if (!students.isEmpty()) {
            events.publishEvent(new StudentJourneyEvent(trip.getRouteIdValue(), trip.getId(), students,
                    fact, title, title + " en el viaje del " + trip.getServiceDate() + "."));
        }
    }
}
