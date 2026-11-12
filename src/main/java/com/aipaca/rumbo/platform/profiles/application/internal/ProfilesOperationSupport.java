package com.aipaca.rumbo.platform.profiles.application.internal;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;


/** Internal cross-aggregate lookups; authorization remains scoped to the authenticated actor. */
public final class ProfilesOperationSupport {
    private ProfilesOperationSupport() { }
    public static Profile requireProfile(ProfileRepository repo, Long profileId, ProfileType requiredType) {
        var profile = repo.findById(profileId).orElseThrow(() ->
                new IllegalArgumentException("Profile not found: " + profileId));
        if (profile.getProfileType() != requiredType)
            throw new IllegalArgumentException("Expected a " + requiredType + " profile");
        return profile;
    }
    public static Student requireStudent(StudentRepository repo, Long id) {
        return repo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Student not found: " + id));
    }
}
