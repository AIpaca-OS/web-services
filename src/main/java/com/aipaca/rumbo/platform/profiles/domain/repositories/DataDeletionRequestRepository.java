package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;

import java.util.List;
import java.util.Optional;

/** Student data deletion request repository port; saving a request does not delete student data. */
public interface DataDeletionRequestRepository {
    Optional<DataDeletionRequest> findById(Long id);

    List<DataDeletionRequest> findAllByRequestedByProfileId(Long requestedByProfileId);

    DataDeletionRequest save(DataDeletionRequest request);
}
