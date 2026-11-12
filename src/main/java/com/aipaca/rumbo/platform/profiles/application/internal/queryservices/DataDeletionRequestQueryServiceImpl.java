package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;
import com.aipaca.rumbo.platform.profiles.application.queryservices.DataDeletionRequestQueryService;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.repositories.DataDeletionRequestRepository;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.*;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true)
public class DataDeletionRequestQueryServiceImpl implements DataDeletionRequestQueryService {
    private final DataDeletionRequestRepository requests;
    private final ProfilesAuthorization auth;
    public DataDeletionRequestQueryServiceImpl(DataDeletionRequestRepository requests, ProfilesAuthorization auth) {
        this.requests=requests;this.auth=auth;
    }
    @Override public Optional<DataDeletionRequest> handle(GetDataDeletionRequestByIdQuery query) {
        var opt=requests.findById(query.deletionRequestId());
        if (opt.isPresent() && !auth.isAdmin()
                && !opt.get().getRequestedByProfileId().equals(auth.currentProfile().getId()))
            throw new ProfilesAccessException("Deletion request is not accessible", false);
        return opt;
    }
    @Override public List<DataDeletionRequest> handle(GetAllDataDeletionRequestsByRequestedByProfileIdQuery query) {
        if (!auth.isAdmin() && !auth.currentProfile().getId().equals(query.requestedByProfileId()))
            throw new ProfilesAccessException("Deletion request list is not accessible", false);
        return requests.findAllByRequestedByProfileId(query.requestedByProfileId());
    }
}
