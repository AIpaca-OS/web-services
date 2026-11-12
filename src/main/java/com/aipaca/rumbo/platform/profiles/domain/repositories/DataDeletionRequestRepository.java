package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;

import java.util.List;
import java.util.Optional;

/**
 * Data deletion request repository port.
 */
public interface DataDeletionRequestRepository {
    Optional<DataDeletionRequest> findById(Long id);

    List<DataDeletionRequest> findAllByRequestedByProfileId(Long requestedByProfileId);

    DataDeletionRequest save(DataDeletionRequest request);
}
