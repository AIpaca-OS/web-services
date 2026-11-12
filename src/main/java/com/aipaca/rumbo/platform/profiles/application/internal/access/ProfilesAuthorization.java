package com.aipaca.rumbo.platform.profiles.application.internal.access;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import org.springframework.stereotype.Component;

/** Centralizes user-bound ownership and student/tutor authorization. */
@Component
public class ProfilesAuthorization {
    private final ActorIdentity actor;
    private final ProfileRepository profiles;
    private final StudentRepository students;

    public ProfilesAuthorization(ActorIdentity actor, ProfileRepository profiles, StudentRepository students) {
        this.actor = actor;
        this.profiles = profiles;
        this.students = students;
    }

    public Long currentAccountId() { return actor.currentAccountId(); }

    public Profile currentProfile() {
        return profiles.findByAccountId(new AccountId(actor.currentAccountId()))
                .orElseThrow(() -> new ProfilesAccessException("Account has no Rumbo profile", false));
    }

    public Profile currentTutor() {
        var p = currentProfile();
        if (p.getProfileType() != ProfileType.TUTOR)
            throw new ProfilesAccessException("Tutor profile required", false);
        return p;
    }

    public Profile currentDriver() {
        var p = currentProfile();
        if (p.getProfileType() != ProfileType.DRIVER)
            throw new ProfilesAccessException("Driver profile required", false);
        return p;
    }

    public Student requireStudentAccess(Long studentId) {
        var student = students.findById(studentId).orElseThrow(() ->
                new ProfilesAccessException("Student is not accessible", false));
        if (!student.hasAuthorizedTutor(currentTutor().getId()))
            throw new ProfilesAccessException("Student is not accessible", false);
        return student;
    }

    public void requireMyProfile(Long profileId) {
        if (!currentProfile().getId().equals(profileId))
            throw new ProfilesAccessException("Profile is not accessible", false);
    }

    public void requireAdmin() {
        // IAM must attach verified authorities to the authenticated principal.
        actor.currentAccountId();
        if (!actor.hasRole("ADMIN"))
            throw new ProfilesAccessException("Administrator privileges required", false);
    }

    public boolean isAdmin() {
        actor.currentAccountId();
        return actor.hasRole("ADMIN");
    }
}
