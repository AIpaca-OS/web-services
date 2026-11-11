package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Spring Data JPA operations for profile persistence entities. */
@Repository
public interface ProfilePersistenceRepository extends JpaRepository<ProfilePersistenceEntity, Long> {
    @Query("select profile from ProfilePersistenceEntity profile where profile.accountId = :accountId")
    Optional<ProfilePersistenceEntity> findByAccountId(@Param("accountId") AccountId accountId);

    @Query("select count(profile) from ProfilePersistenceEntity profile where profile.accountId = :accountId")
    long countByAccountId(@Param("accountId") AccountId accountId);
}
