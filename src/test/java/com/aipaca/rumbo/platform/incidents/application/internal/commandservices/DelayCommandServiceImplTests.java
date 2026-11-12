package com.aipaca.rumbo.platform.incidents.application.internal.commandservices;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.DelayStatus;
import com.aipaca.rumbo.platform.incidents.domain.repositories.DelayRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class DelayCommandServiceImplTests {
    private final DelayRepository delays = mock(DelayRepository.class);
    private final DelayCommandServiceImpl service = new DelayCommandServiceImpl(delays);

    private static String errorCode(Result<Long, ApplicationError> result) {
        var failure = assertInstanceOf(Result.Failure.class, result);
        return ((ApplicationError) failure.error()).code();
    }

    private static Delay withId(Long id, Delay delay) {
        return Delay.reconstitute(id, delay.getTripId(), delay.getReportedByProfileId(), delay.getCause(),
                delay.getEstimatedMinutes(), delay.getStatus(), delay.getPreviousDelayId(), delay.getReportedAt());
    }

    @Test
    void updateSupersedesThePreviousDelayAndReturnsTheNewId() {
        var previous = withId(10L, new Delay(new ReportDelayCommand(1L, 2L, "Tráfico", 15)));
        when(delays.findById(10L)).thenReturn(Optional.of(previous));
        when(delays.save(any())).thenAnswer(invocation -> {
            Delay delay = invocation.getArgument(0);
            return delay.getId() == null ? withId(11L, delay) : delay;
        });
        var result = service.handle(new UpdateDelayCommand(10L, 2L, "Tráfico", 25));
        assertEquals(11L, assertInstanceOf(Result.Success.class, result).value());
        assertEquals(DelayStatus.SUPERSEDED, previous.getStatus());
        verify(delays).save(argThat(delay -> delay.getId() == null && Long.valueOf(10L).equals(delay.getPreviousDelayId())));
    }

    @Test
    void updatingASupersededDelayIsABusinessRuleViolation() {
        var previous = withId(10L, new Delay(new ReportDelayCommand(1L, 2L, "Tráfico", 15)));
        previous.supersede();
        when(delays.findById(10L)).thenReturn(Optional.of(previous));
        assertEquals("BUSINESS_RULE_VIOLATION", errorCode(service.handle(new UpdateDelayCommand(10L, 2L, "Tráfico", 25))));
        verify(delays, never()).save(any());
    }

    @Test
    void updatingAMissingDelayIsNotFound() {
        when(delays.findById(10L)).thenReturn(Optional.empty());
        assertEquals("DELAY_NOT_FOUND", errorCode(service.handle(new UpdateDelayCommand(10L, 2L, "Tráfico", 25))));
    }
}
