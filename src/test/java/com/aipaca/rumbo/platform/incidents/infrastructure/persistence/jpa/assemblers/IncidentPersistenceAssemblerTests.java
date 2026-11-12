package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidentPersistenceAssemblerTests {

    @Test
    void acknowledgementsReferenceTheIncidentAndRoundTrip() {
        var incident = new Incident(new ReportIncidentCommand(1L, 2L, 5L, IncidentCategory.STUDENT_HEALTH, "Mareo"));
        incident.acknowledge(new ProfileId(7L));

        var entity = IncidentPersistenceAssembler.toPersistenceFromDomain(incident);
        assertNull(entity.getId());
        assertSame(entity, entity.getAcknowledgements().getFirst().getIncident());

        var restored = IncidentPersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(1L, restored.getTripIdValue());
        assertEquals(5L, restored.getStudentIdValue());
        assertTrue(restored.isAcknowledgedBy(new ProfileId(7L)));
    }
}
