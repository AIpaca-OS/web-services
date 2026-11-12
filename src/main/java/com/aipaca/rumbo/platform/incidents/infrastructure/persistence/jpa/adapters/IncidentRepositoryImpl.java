package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.domain.repositories.IncidentRepository;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.assemblers.IncidentPersistenceAssembler;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.repositories.IncidentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain incident repository port with Spring Data JPA.
 */
@Repository
public class IncidentRepositoryImpl implements IncidentRepository {

    private final IncidentPersistenceRepository incidentPersistenceRepository;

    public IncidentRepositoryImpl(IncidentPersistenceRepository incidentPersistenceRepository) {
        this.incidentPersistenceRepository = incidentPersistenceRepository;
    }

    @Override
    public Optional<Incident> findById(Long id) {
        return incidentPersistenceRepository.findById(id).map(IncidentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Incident> findAllByTripId(TripId tripId) {
        return incidentPersistenceRepository.findAllByTripIdOrderByReportedAtDesc(tripId).stream()
                .map(IncidentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Incident save(Incident incident) {
        var savedEntity = incidentPersistenceRepository.save(IncidentPersistenceAssembler.toPersistenceFromDomain(incident));
        return IncidentPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
