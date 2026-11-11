package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities.DelayPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for delay persistence entities.
 */
@Repository
public interface DelayPersistenceRepository extends JpaRepository<DelayPersistenceEntity, Long> {
    List<DelayPersistenceEntity> findAllByTripIdOrderByReportedAtDesc(TripId tripId);
}
