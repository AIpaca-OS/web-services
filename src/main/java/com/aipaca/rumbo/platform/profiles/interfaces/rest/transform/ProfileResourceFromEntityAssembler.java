package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.ProfileResource;
public final class ProfileResourceFromEntityAssembler {
    private ProfileResourceFromEntityAssembler() { }
    public static ProfileResource toResourceFromEntity(Profile p) {
        return new ProfileResource(p.getId(),p.getAccountId().accountId(),p.getProfileType(),p.getName().firstName(),p.getName().lastName(),p.getPhoneNumber());
    }
}
