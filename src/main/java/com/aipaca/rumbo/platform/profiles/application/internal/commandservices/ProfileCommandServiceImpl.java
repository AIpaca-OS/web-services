package com.aipaca.rumbo.platform.profiles.application.internal.commandservices;

import com.aipaca.rumbo.platform.profiles.application.commandservices.ProfileCommandService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.UpdateProfileContactCommand;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.shared.application.result.ApplicationError;
import com.aipaca.rumbo.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Profile Command Service Implementation
 */
@Service
@Transactional
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profileRepository;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     */
    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(CreateProfileCommand command) {
        if (profileRepository.existsByAccountId(command.accountId())) {
            return Result.failure(ApplicationError.conflict("Profile", "Account already has a profile"));
        }
        try {
            var savedProfile = profileRepository.save(new Profile(command));
            return Result.success(savedProfile.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-profile", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    public Result<Long, ApplicationError> handle(UpdateProfileContactCommand command) {
        var result = profileRepository.findById(command.profileId());
        if (result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Profile", command.profileId().toString()));
        }
        var profile = result.get();
        try {
            profile.updateContact(command.phoneNumber());
            return Result.success(profileRepository.save(profile).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-profile-contact", e.getMessage()));
        }
    }
}
