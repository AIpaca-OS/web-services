package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;
import com.aipaca.rumbo.platform.billing.domain.repositories.PlanRepository;
import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import com.aipaca.rumbo.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the plan domain repository port
 * with Spring Data JPA.
 */
@Repository
public class PlanRepositoryImpl implements PlanRepository {

    private final PlanPersistenceRepository planPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public PlanRepositoryImpl(
            PlanPersistenceRepository planPersistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.planPersistenceRepository = planPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Plan> findById(Long id) {
        return planPersistenceRepository.findById(id)
                .map(PlanPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Plan> findAll() {
        return planPersistenceRepository.findAll()
                .stream()
                .map(PlanPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Plan> findByName(String name) {
        return planPersistenceRepository.findByName(name)
                .map(PlanPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Plan save(Plan plan) {
        var entity =
                PlanPersistenceAssembler.toPersistenceFromDomain(plan);

        var saved = planPersistenceRepository.save(entity);

        var savedPlan =
                PlanPersistenceAssembler.toDomainFromPersistence(saved);

        domainEventPublisher.publishAndClearEvents(plan);

        return savedPlan;
    }

    @Override
    public boolean existsById(Long id) {
        return planPersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return planPersistenceRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, Long id) {
        return planPersistenceRepository
                .existsByNameAndIdIsNot(name, id);
    }

    @Override
    public void deleteById(Long id) {
        planPersistenceRepository.deleteById(id);
    }
}