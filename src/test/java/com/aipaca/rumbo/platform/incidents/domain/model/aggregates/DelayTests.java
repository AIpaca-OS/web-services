package com.aipaca.rumbo.platform.incidents.domain.model.aggregates;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.DelayStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DelayTests {

    private static Delay persistedDelay() {
        var delay = new Delay(new ReportDelayCommand(1L, 2L, "Tráfico", 15));
        return Delay.reconstitute(10L, delay.getTripId(), delay.getReportedByProfileId(), delay.getCause(),
                delay.getEstimatedMinutes(), delay.getStatus(), null, delay.getReportedAt());
    }

    @Test
    void reportedDelayIsActiveWithoutPreviousDelay() {
        var delay = new Delay(new ReportDelayCommand(1L, 2L, " Tráfico ", 15));
        assertEquals(DelayStatus.ACTIVE, delay.getStatus());
        assertEquals("Tráfico", delay.getCause());
        assertEquals(1L, delay.getTripIdValue());
        assertNull(delay.getPreviousDelayId());
    }

    @Test
    void estimatedMinutesAndCauseAreValidated() {
        assertThrows(IllegalArgumentException.class, () -> new Delay(new ReportDelayCommand(1L, 2L, "Tráfico", 0)));
        assertThrows(IllegalArgumentException.class, () -> new Delay(new ReportDelayCommand(1L, 2L, "Tráfico", 601)));
        assertThrows(IllegalArgumentException.class, () -> new Delay(new ReportDelayCommand(1L, 2L, " ", 10)));
    }

    @Test
    void updateCreatesANewEstimationThatReferencesThePreviousOne() {
        var previous = persistedDelay();
        var update = new Delay(new UpdateDelayCommand(10L, 2L, "Tráfico", 25), previous);
        previous.supersede();
        assertEquals(10L, update.getPreviousDelayId());
        assertEquals(previous.getTripId(), update.getTripId());
        assertEquals(25, update.getEstimatedMinutes());
        assertEquals(DelayStatus.SUPERSEDED, previous.getStatus());
        assertThrows(IllegalArgumentException.class,
                () -> new Delay(new UpdateDelayCommand(10L, 2L, "Tráfico", 30), previous));
    }
}
