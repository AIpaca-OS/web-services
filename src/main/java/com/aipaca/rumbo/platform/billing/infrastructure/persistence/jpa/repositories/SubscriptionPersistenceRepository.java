package com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.billing.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.billing.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPersistenceRepository
        extends JpaRepository<SubscriptionPersistenceEntity, Long> {

    Optional<SubscriptionPersistenceEntity> findFirstByDriverProfileIdAndPlanIdOrderByIdDesc(
            DriverProfileId driverProfileId, Long planId);

    List<SubscriptionPersistenceEntity> findAllByDriverProfileIdOrderByIdDesc(DriverProfileId driverProfileId);
}