package com.aipaca.rumbo.platform.routes.application.commandservices;
import com.aipaca.rumbo.platform.routes.domain.model.Trip;
import com.aipaca.rumbo.platform.routes.domain.model.commands.*;

public interface TripCommandService {
    Trip handle(PlanTripCommand command);
    Trip handle(MarkAbsenceCommand command);
    Trip handle(StartTripCommand command);
    Trip handle(ServeStopCommand command);
    Trip handle(CancelTripCommand command);
    Trip handle(CompleteTripCommand command);
    Trip handle(ConfirmStudentArrivalCommand command);
}
