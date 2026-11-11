package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;

import com.aipaca.rumbo.platform.profiles.application.queryservices.DataDeletionRequestQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllDataDeletionRequestsByRequestedByProfileIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetDataDeletionRequestByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.repositories.DataDeletionRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Data Deletion Request Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class DataDeletionRequestQueryServiceImpl implements DataDeletionRequestQueryService {
    private final DataDeletionRequestRepository dataDeletionRequestRepository;

    /**
     * Constructor
     *
     * @param dataDeletionRequestRepository The {@link DataDeletionRequestRepository} instance
     */
    public DataDeletionRequestQueryServiceImpl(DataDeletionRequestRepository dataDeletionRequestRepository) {
        this.dataDeletionRequestRepository = dataDeletionRequestRepository;
    }

    // inherited javadoc
    @Override
    public Optional<DataDeletionRequest> handle(GetDataDeletionRequestByIdQuery query) {
        return dataDeletionRequestRepository.findById(query.deletionRequestId());
    }

    // inherited javadoc
    @Override
    public List<DataDeletionRequest> handle(GetAllDataDeletionRequestsByRequestedByProfileIdQuery query) {
        return dataDeletionRequestRepository.findAllByRequestedByProfileId(query.requestedByProfileId());
    }
}
