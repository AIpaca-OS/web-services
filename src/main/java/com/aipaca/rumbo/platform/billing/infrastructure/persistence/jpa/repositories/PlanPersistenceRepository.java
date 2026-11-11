package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for plan persistence entities.
 */
@Repository
public interface PlanPersistenceRepository
        extends JpaRepository<PlanPersistenceEntity, Long> {

    Optional<PlanPersistenceEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, Long id);
}