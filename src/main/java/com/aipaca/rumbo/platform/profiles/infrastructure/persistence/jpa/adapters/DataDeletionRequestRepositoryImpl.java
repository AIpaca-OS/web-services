package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.repositories.DataDeletionRequestRepository;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.DataDeletionRequestPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.DataDeletionRequestPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository adapter: no approval, actor authorization or erasure logic here. */
@Repository
public class DataDeletionRequestRepositoryImpl implements DataDeletionRequestRepository {
    private final DataDeletionRequestPersistenceRepository persistenceRepository;

    public DataDeletionRequestRepositoryImpl(DataDeletionRequestPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override

    public Optional<DataDeletionRequest> findById(Long id) {
        return persistenceRepository.findById(id)
                .map(DataDeletionRequestPersistenceAssembler::toDomainFromPersistence);
    }

    @Override

    public List<DataDeletionRequest> findAllByRequestedByProfileId(Long requestedByProfileId) {
        return persistenceRepository.findAllByRequestedByProfileId(requestedByProfileId).stream()
                .map(DataDeletionRequestPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override

    public DataDeletionRequest save(DataDeletionRequest request) {
        var saved = persistenceRepository.save(
                DataDeletionRequestPersistenceAssembler.toPersistenceFromDomain(request));
        return DataDeletionRequestPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
