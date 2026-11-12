package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;

import com.aipaca.rumbo.platform.profiles.application.queryservices.ProfileQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByAccountIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Profile Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class ProfileQueryServiceImpl implements ProfileQueryService {
    private final ProfileRepository profileRepository;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     */
    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId());
    }

    // inherited javadoc
    @Override
    public Optional<Profile> handle(GetProfileByAccountIdQuery query) {
        return profileRepository.findByAccountId(new AccountId(query.accountId()));
    }
}
