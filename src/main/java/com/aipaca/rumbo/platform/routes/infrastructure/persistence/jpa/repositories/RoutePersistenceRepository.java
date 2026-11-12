package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.DriverProfileId;
import com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.entities.RoutePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for route persistence entities.
 */
@Repository
public interface RoutePersistenceRepository extends JpaRepository<RoutePersistenceEntity, Long> {
    List<RoutePersistenceEntity> findAllByDriverProfileId(DriverProfileId driverProfileId);
}
