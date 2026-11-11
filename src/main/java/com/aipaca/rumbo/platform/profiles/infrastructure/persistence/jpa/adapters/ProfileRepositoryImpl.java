package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Implements the profile domain port using the JPA repository and persistence assembler. */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfilePersistenceRepository profilePersistenceRepository;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository) {
        this.profilePersistenceRepository = profilePersistenceRepository;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profilePersistenceRepository.findById(id)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByAccountId(AccountId accountId) {
        return profilePersistenceRepository.findByAccountId(accountId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByAccountId(AccountId accountId) {
        return profilePersistenceRepository.countByAccountId(accountId) > 0;
    }

    @Override
    public Profile save(Profile profile) {
        var savedEntity = profilePersistenceRepository.save(
                ProfilePersistenceAssembler.toPersistenceFromDomain(profile));
        return ProfilePersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
