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
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Trip Command Service Implementation
 */
@Service
@Transactional
public class TripCommandServiceImpl implements TripCommandService {
    private final TripRepository tripRepository;

    /**
     * Constructor
     *
     * @param tripRepository The {@link TripRepository} instance
     */
    public TripCommandServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(PlanTripCommand command) {
        if (tripRepository.existsByRouteIdAndServiceDate(new RouteId(command.routeId()), command.serviceDate()))
            return Result.failure(ApplicationError.conflict("Trip",
                    "A trip is already scheduled for %s".formatted(command.serviceDate())));
        try {
            var trip = new Trip(command);
            return Result.success(tripRepository.save(trip).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Trip", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("plan-trip", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(StartTripCommand command) {
        return transition(command.tripId(), "start-trip", Trip::start);
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(MarkStudentAbsenceCommand command) {
        return transition(command.tripId(), "mark-student-absence",
                trip -> trip.markStudentAbsent(new StudentId(command.studentId())));
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ServeTripStopCommand command) {
        return transition(command.tripId(), "serve-trip-stop", trip -> trip.serveStop(command.stopId()));
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ConfirmStudentArrivalCommand command) {
        return transition(command.tripId(), "confirm-student-arrival",
                trip -> trip.confirmStudentArrival(new StudentId(command.studentId())));
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CancelTripCommand command) {
        return transition(command.tripId(), "cancel-trip", trip -> trip.cancel(command.reason()));
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CompleteTripCommand command) {
        return transition(command.tripId(), "complete-trip", Trip::complete);
    }

    /**
     * Validates that the trip exists, applies the domain transition and saves the trip.
     */
    private Result<Long, ApplicationError> transition(Long tripId, String context, Consumer<Trip> change) {
        var result = tripRepository.findById(tripId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Trip", tripId.toString()));
        var trip = result.get();
        try {
            change.accept(trip);
            return Result.success(tripRepository.save(trip).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation(context, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(context, e.getMessage()));
        }
    }
}
