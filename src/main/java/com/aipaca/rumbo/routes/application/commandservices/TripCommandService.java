package com.aipaca.rumbo.routes.application.commandservices;
import com.aipaca.rumbo.routes.domain.model.Trip;
import com.aipaca.rumbo.routes.domain.model.commands.*;
public interface TripCommandService {
    Trip handle(PlanTripCommand command);
    Trip handle(MarkAbsenceCommand command);
    Trip handle(StartTripCommand command);
    Trip handle(ServeStopCommand command);
    Trip handle(CancelTripCommand command);
    Trip handle(CompleteTripCommand command);
}
