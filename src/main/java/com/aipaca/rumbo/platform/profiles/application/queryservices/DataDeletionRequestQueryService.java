package com.aipaca.rumbo.platform.profiles.application.queryservices;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllDataDeletionRequestsByRequestedByProfileIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetDataDeletionRequestByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Data Deletion Request Query Service
 */
public interface DataDeletionRequestQueryService {
    /**
     * Handle Get Data Deletion Request By ID Query
     *
     * @param query The {@link GetDataDeletionRequestByIdQuery} Query
     * @return A {@link DataDeletionRequest} instance if found, otherwise empty
     */
    Optional<DataDeletionRequest> handle(GetDataDeletionRequestByIdQuery query);

    /**
     * Handle Get All Data Deletion Requests By Requested By Profile ID Query
     *
     * @param query The {@link GetAllDataDeletionRequestsByRequestedByProfileIdQuery} Query
     * @return A list of {@link DataDeletionRequest} instances
     */
    List<DataDeletionRequest> handle(GetAllDataDeletionRequestsByRequestedByProfileIdQuery query);
}
