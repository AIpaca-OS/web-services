package com.aipaca.rumbo.platform.trips.application.internal.commandservices;

import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import com.aipaca.rumbo.platform.trips.domain.model.commands.PlanTripCommand;
import com.aipaca.rumbo.platform.trips.domain.model.commands.StartTripCommand;
import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteId;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.RouteShift;
import com.aipaca.rumbo.platform.trips.domain.model.valueobjects.StudentId;
import com.aipaca.rumbo.platform.trips.domain.repositories.TripRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TripCommandServiceImplTests {
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 16);
    private final TripRepository trips = mock(TripRepository.class);
    private final TripCommandServiceImpl service = new TripCommandServiceImpl(trips);

    private static String errorCode(Result<Long, ApplicationError> result) {
        var failure = assertInstanceOf(Result.Failure.class, result);
        return ((ApplicationError) failure.error()).code();
    }

    private static PlanTripCommand plan() {
        return new PlanTripCommand(1L, 2L, 3L, MONDAY, RouteShift.MORNING,
                List.of(new TripPassenger(new StudentId(5L), 10L, 1, "Parada 1", null)));
    }

    @Test
    void secondTripForTheSameRouteAndDateIsAConflict() {
        when(trips.existsByRouteIdAndServiceDate(new RouteId(1L), MONDAY)).thenReturn(true);
        assertEquals("TRIP_CONFLICT", errorCode(service.handle(plan())));
        verify(trips, never()).save(any());
    }

    @Test
    void planWithoutStudentsIsAValidationError() {
        var command = new PlanTripCommand(1L, 2L, 3L, MONDAY, RouteShift.MORNING, List.of());
        assertEquals("VALIDATION_ERROR", errorCode(service.handle(command)));
    }

    @Test
    void missingTripIsNotFound() {
        when(trips.findById(9L)).thenReturn(Optional.empty());
        assertEquals("TRIP_NOT_FOUND", errorCode(service.handle(new StartTripCommand(9L))));
    }
}
