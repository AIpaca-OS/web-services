package com.aipaca.rumbo.platform.incidents.domain.model.aggregates;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentStatus;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidentTests {

    private static Incident reported() {
        return new Incident(new ReportIncidentCommand(1L, 2L, 5L, IncidentCategory.MECHANICAL_FAILURE,
                " Falla en el motor "));
    }

    @Test
    void reportedIncidentIsOpenAndKeepsOnlyIdentities() {
        var incident = reported();
        assertEquals(IncidentStatus.OPEN, incident.getStatus());
        assertEquals("Falla en el motor", incident.getDescription());
        assertEquals(1L, incident.getTripIdValue());
        assertEquals(2L, incident.getReportedByProfileIdValue());
        assertEquals(5L, incident.getStudentIdValue());
        assertNotNull(incident.getReportedAt());
        assertTrue(incident.getAcknowledgements().isEmpty());
    }

    @Test
    void categoryAndDescriptionAreRequired() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReportIncidentCommand(1L, 2L, null, null, "Falla"));
        assertThrows(IllegalArgumentException.class,
                () -> new Incident(new ReportIncidentCommand(1L, 2L, null, IncidentCategory.OTHER, " ")));
        assertThrows(IllegalArgumentException.class,
                () -> new ReportIncidentCommand(0L, 2L, null, IncidentCategory.OTHER, "Falla"));
    }

    @Test
    void resolvedIncidentKeepsItsOriginalResolution() {
        var incident = reported();
        incident.resolve("Se cambió de vehículo");
        assertEquals(IncidentStatus.RESOLVED, incident.getStatus());
        assertNotNull(incident.getResolvedAt());
        assertThrows(IllegalArgumentException.class, () -> incident.resolve("Otra resolución"));
        assertThrows(IllegalArgumentException.class, () -> incident.update(IncidentCategory.OTHER, "Cambio"));
        assertEquals("Se cambió de vehículo", incident.getResolution());
    }

    @Test
    void eachTutorAcknowledgesOnlyOnce() {
        var incident = reported();
        incident.acknowledge(new ProfileId(7L));
        assertTrue(incident.isAcknowledgedBy(new ProfileId(7L)));
        assertThrows(IllegalArgumentException.class, () -> incident.acknowledge(new ProfileId(7L)));
        incident.acknowledge(new ProfileId(8L));
        assertEquals(2, incident.getAcknowledgements().size());
    }
}
