package com.aipaca.rumbo.platform.trips.application.commandservices;

import com.aipaca.rumbo.platform.trips.domain.model.commands.*;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Trip Command Service. Every handler returns the id of the affected trip.
 */
public interface TripCommandService {
    Result<Long, ApplicationError> handle(PlanTripCommand command);

    Result<Long, ApplicationError> handle(StartTripCommand command);

    Result<Long, ApplicationError> handle(MarkStudentAbsenceCommand command);

    Result<Long, ApplicationError> handle(ServeTripStopCommand command);

    Result<Long, ApplicationError> handle(ConfirmStudentArrivalCommand command);

    Result<Long, ApplicationError> handle(CancelTripCommand command);

    Result<Long, ApplicationError> handle(CompleteTripCommand command);
}
