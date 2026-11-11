package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.SchoolPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Uses the inherited identity lookup, catalog lookup and save operations of Spring Data JPA. */
@Repository
public interface SchoolPersistenceRepository extends JpaRepository<SchoolPersistenceEntity, Long> {
}
