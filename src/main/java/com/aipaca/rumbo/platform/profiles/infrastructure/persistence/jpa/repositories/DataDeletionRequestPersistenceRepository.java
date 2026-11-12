package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.DataDeletionRequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for data deletion request persistence entities.
 */
@Repository
public interface DataDeletionRequestPersistenceRepository
        extends JpaRepository<DataDeletionRequestPersistenceEntity, Long> {
    List<DataDeletionRequestPersistenceEntity> findAllByRequestedByProfileId(Long requestedByProfileId);
}
