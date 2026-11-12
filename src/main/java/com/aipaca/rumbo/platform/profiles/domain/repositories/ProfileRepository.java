package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;

import java.util.Optional;

/**
 * Profile repository port.
 */
public interface ProfileRepository {
    Optional<Profile> findById(Long id);

    Optional<Profile> findByAccountId(AccountId accountId);

    boolean existsByAccountId(AccountId accountId);

    Profile save(Profile profile);
}
