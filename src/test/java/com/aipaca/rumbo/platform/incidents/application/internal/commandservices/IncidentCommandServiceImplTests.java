package com.aipaca.rumbo.platform.incidents.application.internal.commandservices;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.AcknowledgeIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ResolveIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.IncidentCategory;
import com.aipaca.rumbo.platform.incidents.domain.repositories.IncidentRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidentCommandServiceImplTests {
    private final IncidentRepository incidents = mock(IncidentRepository.class);
    private final IncidentCommandServiceImpl service = new IncidentCommandServiceImpl(incidents);

    private static String errorCode(Result<Long, ApplicationError> result) {
        var failure = assertInstanceOf(Result.Failure.class, result);
        return ((ApplicationError) failure.error()).code();
    }

    private static Incident persisted(Incident incident) {
        return Incident.reconstitute(3L, incident.getTripId(), incident.getReportedByProfileId(),
                incident.getStudentId(), incident.getCategory(), incident.getDescription(), incident.getStatus(),
                incident.getResolution(), incident.getReportedAt(), incident.getResolvedAt(),
                incident.getAcknowledgements());
    }

    private static Incident openIncident() {
        return persisted(new Incident(new ReportIncidentCommand(1L, 2L, null, IncidentCategory.ROAD_BLOCKED,
                "Vía cerrada")));
    }

    @Test
    void reportReturnsTheGeneratedId() {
        when(incidents.save(any())).thenAnswer(invocation -> persisted(invocation.getArgument(0)));
        var result = service.handle(new ReportIncidentCommand(1L, 2L, null, IncidentCategory.OTHER, "Imprevisto"));
        assertEquals(3L, assertInstanceOf(Result.Success.class, result).value());
    }

    @Test
    void blankDescriptionIsAValidationError() {
        var result = service.handle(new ReportIncidentCommand(1L, 2L, null, IncidentCategory.OTHER, " "));
        assertEquals("VALIDATION_ERROR", errorCode(result));
        verify(incidents, never()).save(any());
    }

    @Test
    void resolvingAMissingIncidentIsNotFound() {
        when(incidents.findById(3L)).thenReturn(Optional.empty());
        assertEquals("INCIDENT_NOT_FOUND", errorCode(service.handle(new ResolveIncidentCommand(3L, "Resuelto"))));
    }

    @Test
    void resolvingTwiceIsABusinessRuleViolation() {
        var incident = openIncident();
        incident.resolve("Resuelto");
        when(incidents.findById(3L)).thenReturn(Optional.of(incident));
        var code = errorCode(service.handle(new ResolveIncidentCommand(3L, "Otra vez")));
        assertEquals("BUSINESS_RULE_VIOLATION", code);
        verify(incidents, never()).save(any());
    }

    @Test
    void acknowledgementIsSavedWithTheIncident() {
        var incident = openIncident();
        when(incidents.findById(3L)).thenReturn(Optional.of(incident));
        when(incidents.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var result = service.handle(new AcknowledgeIncidentCommand(3L, 7L));
        assertEquals(3L, assertInstanceOf(Result.Success.class, result).value());
        assertEquals(1, incident.getAcknowledgements().size());
    }
}
