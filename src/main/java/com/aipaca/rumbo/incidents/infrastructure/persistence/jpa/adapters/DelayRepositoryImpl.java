package com.aipaca.rumbo.incidents.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.domain.repositories.DelayRepository;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.assemblers.DelayPersistenceAssembler;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.repositories.DelayPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain delay repository port with Spring Data JPA.
 */
@Repository
public class DelayRepositoryImpl implements DelayRepository {

    private final DelayPersistenceRepository delayPersistenceRepository;

    public DelayRepositoryImpl(DelayPersistenceRepository delayPersistenceRepository) {
        this.delayPersistenceRepository = delayPersistenceRepository;
    }

    @Override
    public Optional<Delay> findById(Long id) {
        return delayPersistenceRepository.findById(id).map(DelayPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Delay> findAllByTripId(TripId tripId) {
        return delayPersistenceRepository.findAllByTripIdOrderByReportedAtDesc(tripId).stream()
                .map(DelayPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Delay save(Delay delay) {
        var savedEntity = delayPersistenceRepository.save(DelayPersistenceAssembler.toPersistenceFromDomain(delay));
        return DelayPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
