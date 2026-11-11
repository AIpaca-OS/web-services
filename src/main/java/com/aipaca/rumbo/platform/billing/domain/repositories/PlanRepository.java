package com.aipaca.rumbo.platform.billing.domain.repositories;

import com.aipaca.rumbo.platform.billing.domain.model.aggregates.Plan;

import java.util.List;
import java.util.Optional;

/**
 * Subscription plan repository port.
 */
public interface PlanRepository {

    Optional<Plan> findById(Long id);

    List<Plan> findAll();

    Optional<Plan> findByName(String name);

    Plan save(Plan plan);

    boolean existsById(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, Long id);

    void deleteById(Long id);
}