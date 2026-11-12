package com.aipaca.rumbo.platform.incidents.application.internal.commandservices;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Incident Command Service Implementation
 */
@Service
@Transactional
public class IncidentCommandServiceImpl implements IncidentCommandService {
    private final IncidentRepository incidentRepository;

    /**
     * Constructor
     *
     * @param incidentRepository The {@link IncidentRepository} instance
     */
    public IncidentCommandServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ReportIncidentCommand command) {
        try {
            var savedIncident = incidentRepository.save(new Incident(command));
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
                incident -> incident.update(command.category(), command.description()));
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ResolveIncidentCommand command) {
        return change(command.incidentId(), "resolve-incident", incident -> incident.resolve(command.resolution()));
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(AcknowledgeIncidentCommand command) {
        return change(command.incidentId(), "acknowledge-incident",
                incident -> incident.acknowledge(new ProfileId(command.tutorProfileId())));
    }

    /**
     * Validates that the incident exists, applies the domain change and saves the incident.
     */
    private Result<Long, ApplicationError> change(Long incidentId, String context, Consumer<Incident> change) {
        var result = incidentRepository.findById(incidentId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Incident", incidentId.toString()));
        var incident = result.get();
        try {
            change.accept(incident);
            return Result.success(incidentRepository.save(incident).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.businessRuleViolation(context, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(context, e.getMessage()));
        }
    }
}
