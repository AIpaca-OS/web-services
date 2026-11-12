package com.aipaca.rumbo.incidents.application.internal.commandservices;

import com.aipaca.rumbo.platform.incidents.application.commandservices.DelayCommandService;
import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.repositories.DelayRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import com.aipaca.rumbo.platform.shared.application.events.StudentJourneyEvent;
import com.aipaca.rumbo.platform.incidents.application.internal.outboundservices.acl.TripReportAccessAcl;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Delay Command Service Implementation
 */
@Service
@Transactional
public class DelayCommandServiceImpl implements DelayCommandService {
    private final DelayRepository delayRepository;
    private final ApplicationEventPublisher events;
    private final TripReportAccessAcl tripAccess;

    /**
     * Constructor
     *
     * @param delayRepository The {@link DelayRepository} instance
     */
    public DelayCommandServiceImpl(DelayRepository delayRepository, ApplicationEventPublisher events,
                                   TripReportAccessAcl tripAccess) {
        this.delayRepository = delayRepository;
        this.events = events;
        this.tripAccess = tripAccess;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ReportDelayCommand command) {
        if (!tripAccess.canReport(command.tripId(), command.reportedByProfileId(), null))
            return Result.failure(ApplicationError.businessRuleViolation("Report delay",
                    "An IN_PROGRESS trip operated by this driver is required"));
        try {
            var savedDelay = delayRepository.save(new Delay(command));
            announce(savedDelay, "Retraso reportado");
            return Result.success(savedDelay.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Delay", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("report-delay", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(UpdateDelayCommand command) {
        var result = delayRepository.findById(command.delayId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("Delay", command.delayId().toString()));
        var previousDelay = result.get();
        if (!previousDelay.isActive())
            return Result.failure(ApplicationError.businessRuleViolation("update-delay",
                    "Only the active delay can be updated"));
        if (!tripAccess.canReport(previousDelay.getTripIdValue(), command.reportedByProfileId(), null))
            return Result.failure(ApplicationError.businessRuleViolation("Update delay",
                    "An IN_PROGRESS trip operated by this driver is required"));
        try {
            var delay = new Delay(command, previousDelay);
            previousDelay.supersede();
            delayRepository.save(previousDelay);
            var saved = delayRepository.save(delay);
            announce(saved, "Estimación del retraso actualizada");
            return Result.success(saved.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Delay", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-delay", e.getMessage()));
        }
    }

    private void announce(Delay delay, String title) {
        events.publishEvent(new StudentJourneyEvent(null, delay.getTripIdValue(), List.of(),
                "DELAY", title, delay.getCause() + " (" + delay.getEstimatedMinutes() + " minutos)."));
    }
}
