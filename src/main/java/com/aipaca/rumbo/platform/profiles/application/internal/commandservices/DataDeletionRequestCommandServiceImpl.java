package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;
import com.aipaca.rumbo.platform.profiles.application.commandservices.DataDeletionRequestCommandService;
import com.aipaca.rumbo.platform.profiles.application.internal.ProfilesResults;
import com.aipaca.rumbo.platform.profiles.application.security.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.DeletionRequestStatus;
import com.aipaca.rumbo.platform.profiles.domain.repositories.DataDeletionRequestRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class DataDeletionRequestCommandServiceImpl implements DataDeletionRequestCommandService {
    private final DataDeletionRequestRepository requests;
    private final ProfilesAuthorization auth;
    public DataDeletionRequestCommandServiceImpl(DataDeletionRequestRepository requests, ProfilesAuthorization auth) {
        this.requests=requests;this.auth=auth;
    }
    @Override @Transactional
    public Result<Long,ApplicationError> handle(RequestStudentDataDeletionCommand command) {
        var tutor=auth.currentTutor();
        if (!tutor.getId().equals(command.requestedByProfileId()))
            return ProfilesResults.conflict("DELETION_REQUEST","Requester must be authenticated tutor");
        auth.requireStudentAccess(command.studentId());
        // US08: a trip in progress does NOT prevent registering the pending request.
        return Result.success(requests.save(new DataDeletionRequest(command)).getId());
    }

    @Override @Transactional
    public Result<Long,ApplicationError> handle(ResolveDataDeletionRequestCommand command) {
        auth.requireAdmin();
        var opt=requests.findById(command.deletionRequestId());
        if (opt.isEmpty()) return ProfilesResults.notFound("DELETION_REQUEST",command.deletionRequestId());
        var request=opt.get();
        // The Trip Execution gateway and legally approved PII erasure policy do not exist yet.
        // Fail closed. Never record APPROVED while student PII is retained.
        if (command.status()==DeletionRequestStatus.APPROVED)
            return ProfilesResults.conflict("DELETION_REQUEST",
                    "Approval unavailable: Trip Execution verification and personal-data erasure are not integrated");
        try { request.reject(command.resolutionNote(), Instant.now()); }
        catch (IllegalStateException ex) { return ProfilesResults.domainFailure(ex); }
        return Result.success(requests.save(request).getId());
    }
}
