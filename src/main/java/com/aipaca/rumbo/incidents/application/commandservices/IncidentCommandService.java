package com.aipaca.rumbo.incidents.application.commandservices;

import com.aipaca.rumbo.platform.incidents.domain.model.commands.AcknowledgeIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ResolveIncidentCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateIncidentCommand;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;

/**
 * Incident Command Service. Every handler returns the id of the affected incident.
 */
public interface IncidentCommandService {
    Result<Long, ApplicationError> handle(ReportIncidentCommand command);

    Result<Long, ApplicationError> handle(UpdateIncidentCommand command);

    Result<Long, ApplicationError> handle(ResolveIncidentCommand command);

    Result<Long, ApplicationError> handle(AcknowledgeIncidentCommand command);
}
