package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for incident persistence entities.
 */
@Repository
public interface IncidentPersistenceRepository extends JpaRepository<IncidentPersistenceEntity, Long> {
    List<IncidentPersistenceEntity> findAllByTripIdOrderByReportedAtDesc(TripId tripId);
}
