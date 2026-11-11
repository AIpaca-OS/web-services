package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;
import com.aipaca.rumbo.platform.profiles.application.commandservices.ProfileCommandService;
import com.aipaca.rumbo.platform.profiles.application.internal.ProfilesResults;
import com.aipaca.rumbo.platform.profiles.application.security.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profiles;
    private final ProfilesAuthorization authorization;
    public ProfileCommandServiceImpl(ProfileRepository profiles, ProfilesAuthorization authorization) {
        this.profiles=profiles; this.authorization=authorization;
    }
    @Override @Transactional
    public Result<Long,ApplicationError> handle(CreateProfileCommand command) {
        if (!authorization.currentAccountId().equals(command.accountId().accountId()))
            return ProfilesResults.conflict("PROFILE", "Cannot register a profile for another IAM account");
        if (profiles.existsByAccountId(command.accountId()))
            return ProfilesResults.conflict("PROFILE", "Account already has a profile");
        return Result.success(profiles.save(new Profile(command)).getId());
    }
    @Override @Transactional
    public Result<Long,ApplicationError> handle(UpdateProfileContactCommand command) {
        authorization.requireMyProfile(command.profileId());
        var p=profiles.findById(command.profileId());
        if (p.isEmpty()) return ProfilesResults.notFound("PROFILE",command.profileId());
        p.get().updateContact(command.phoneNumber());
        return Result.success(profiles.save(p.get()).getId());
    }
}
