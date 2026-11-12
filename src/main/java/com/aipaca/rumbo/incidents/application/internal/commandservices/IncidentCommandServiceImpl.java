package com.aipaca.rumbo.incidents.application.internal.commandservices;

import com.aipaca.rumbo.platform.incidents.application.commandservices.IncidentCommandService;
import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.AcknowledgeIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ResolveIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.ProfileId;
import com.aipaca.rumbo.platform.incidents.domain.repositories.IncidentRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import com.aipaca.rumbo.platform.shared.application.events.StudentJourneyEvent;
import com.aipaca.rumbo.platform.incidents.application.internal.outboundservices.acl.TripReportAccessAcl;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Incident Command Service Implementation
 */
@Service
@Transactional
public class IncidentCommandServiceImpl implements IncidentCommandService {
    private final IncidentRepository incidentRepository;
    private final ApplicationEventPublisher events;
    private final TripReportAccessAcl tripAccess;

    /**
     * Constructor
     *
     * @param incidentRepository The {@link IncidentRepository} instance
     */
    public IncidentCommandServiceImpl(IncidentRepository incidentRepository, ApplicationEventPublisher events,
                                      TripReportAccessAcl tripAccess) {
        this.incidentRepository = incidentRepository;
        this.events = events;
        this.tripAccess = tripAccess;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ReportIncidentCommand command) {
        if (!tripAccess.canReport(command.tripId(), command.reportedByProfileId(), command.studentId()))
            return Result.failure(ApplicationError.businessRuleViolation("Report incident",
                    "An IN_PROGRESS trip operated by this driver and its student are required"));
        try {
            var savedIncident = incidentRepository.save(new Incident(command));
            announce(savedIncident, "Incidencia reportada");
            return Result.success(savedIncident.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Incident", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("report-incident", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(UpdateIncidentCommand command) {
        return change(command.incidentId(), "update-incident",
                incident -> incident.update(command.category(), command.description()), "Incidencia actualizada");
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ResolveIncidentCommand command) {
        return change(command.incidentId(), "resolve-incident", incident -> incident.resolve(command.resolution()), "Incidencia resuelta");
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(AcknowledgeIncidentCommand command) {
        return change(command.incidentId(), "acknowledge-incident",
                incident -> incident.acknowledge(new ProfileId(command.tutorProfileId())), null);
    }

    /**
     * Validates that the incident exists, applies the domain change and saves the incident.
     */
    private Result<Long, ApplicationError> change(Long incidentId, String context,
                                                   Consumer<Incident> change, String notificationTitle) {
        var result = incidentRepository.findById(incidentId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Incident", incidentId.toString()));
        var incident = result.get();
        try {
            change.accept(incident);
            var saved = incidentRepository.save(incident);
            if (notificationTitle != null) announce(saved, notificationTitle);
            return Result.success(saved.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation(context, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(context, e.getMessage()));
        }
    }

    private void announce(Incident incident, String title) {
        events.publishEvent(new StudentJourneyEvent(null, incident.getTripIdValue(), List.of(),
                "INCIDENT", title, incident.getDescription()));
    }
}
