package com.aipaca.rumbo.platform.incidents.application.internal.commandservices;

import com.aipaca.rumbo.platform.incidents.application.commandservices.DelayCommandService;
import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.ReportDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.model.commands.UpdateDelayCommand;
import com.aipaca.rumbo.platform.incidents.domain.repositories.DelayRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Delay Command Service Implementation
 */
@Service
@Transactional
public class DelayCommandServiceImpl implements DelayCommandService {
    private final DelayRepository delayRepository;

    /**
     * Constructor
     *
     * @param delayRepository The {@link DelayRepository} instance
     */
    public DelayCommandServiceImpl(DelayRepository delayRepository) {
        this.delayRepository = delayRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ReportDelayCommand command) {
        try {
            var savedDelay = delayRepository.save(new Delay(command));
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
        try {
            var delay = new Delay(command, previousDelay);
            previousDelay.supersede();
            delayRepository.save(previousDelay);
            return Result.success(delayRepository.save(delay).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Delay", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-delay", e.getMessage()));
        }
    }
}
