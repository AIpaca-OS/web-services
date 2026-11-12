package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;

import com.aipaca.rumbo.platform.profiles.application.commandservices.DataDeletionRequestCommandService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.ResolveDataDeletionRequestCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.DataDeletionRequestRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Data Deletion Request Command Service Implementation
 */
@Service
@Transactional
public class DataDeletionRequestCommandServiceImpl implements DataDeletionRequestCommandService {
    private final DataDeletionRequestRepository dataDeletionRequestRepository;
    private final ProfileRepository profileRepository;
    private final StudentRepository studentRepository;

    /**
     * Constructor
     *
     * @param dataDeletionRequestRepository The {@link DataDeletionRequestRepository} instance
     * @param profileRepository             The {@link ProfileRepository} instance
     * @param studentRepository             The {@link StudentRepository} instance
     */
    public DataDeletionRequestCommandServiceImpl(DataDeletionRequestRepository dataDeletionRequestRepository,
                                                 ProfileRepository profileRepository,
                                                 StudentRepository studentRepository) {
        this.dataDeletionRequestRepository = dataDeletionRequestRepository;
        this.profileRepository = profileRepository;
        this.studentRepository = studentRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(RequestStudentDataDeletionCommand command) {
        var tutor = profileRepository.findById(command.requestedByProfileId());
        if (tutor.isEmpty() || tutor.get().getProfileType() != ProfileType.TUTOR) {
            return Result.failure(ApplicationError.validationError("requestedByProfileId", "Existing TUTOR profile required"));
        }
        var student = studentRepository.findById(command.studentId());
        if (student.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Student", command.studentId().toString()));
        }
        if (!student.get().hasAuthorizedTutor(command.requestedByProfileId())) {
            return Result.failure(ApplicationError.businessRuleViolation("Deletion request",
                    "Requester must be an authorized tutor"));
        }
        try {
            // The pending request is registered even if a trip is in progress.
            var savedRequest = dataDeletionRequestRepository.save(new DataDeletionRequest(command));
            return Result.success(savedRequest.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Deletion request", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("request-data-deletion", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(ResolveDataDeletionRequestCommand command) {
        var result = dataDeletionRequestRepository.findById(command.deletionRequestId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Deletion_Request", command.deletionRequestId().toString()));
        }
        // Approval requires trip verification and an approved personal-data erasure mechanism:
        // an APPROVED request must not be recorded while private student information is retained.
        if (command.status() == DeletionRequestStatus.APPROVED) {
            return Result.failure(ApplicationError.conflict("Deletion_Request",
                    "Approval unavailable: trip verification and personal-data erasure are not integrated"));
        }
        var request = result.get();
        try {
            request.reject(command.resolutionNote(), Instant.now());
            return Result.success(dataDeletionRequestRepository.save(request).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Deletion request", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("Deletion request", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("resolve-data-deletion", e.getMessage()));
        }
    }
}
