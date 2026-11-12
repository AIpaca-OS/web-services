package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPersistenceRepository
        extends JpaRepository<SubscriptionPersistenceEntity, Long> {

    Optional<SubscriptionPersistenceEntity> findFirstByDriverIdAndPlanIdOrderByIdDesc(
            Long driverId, Long planId);

    List<SubscriptionPersistenceEntity> findAllByDriverIdOrderByIdDesc(Long driverId);
}