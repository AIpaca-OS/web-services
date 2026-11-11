package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;
import com.aipaca.rumbo.platform.profiles.application.queryservices.ProfileQueryService;
import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.*;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly=true)
public class ProfileQueryServiceImpl implements ProfileQueryService {
    private final ProfileRepository profiles;
    private final ProfilesAuthorization auth;
    public ProfileQueryServiceImpl(ProfileRepository profiles,ProfilesAuthorization auth) {
        this.profiles=profiles;this.auth=auth;
    }
    @Override public Optional<Profile> handle(GetProfileByIdQuery query) {
        auth.requireMyProfile(query.profileId());
        return profiles.findById(query.profileId());
    }
    @Override public Optional<Profile> handle(GetProfileByAccountIdQuery query) {
        if (!auth.currentAccountId().equals(query.accountId()))
            throw new com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAccessException("Account not accessible",false);
        return profiles.findByAccountId(new AccountId(query.accountId()));
    }
}
