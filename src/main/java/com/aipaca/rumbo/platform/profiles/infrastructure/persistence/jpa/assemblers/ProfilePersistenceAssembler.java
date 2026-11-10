package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;

/** Translates profile domain and persistence representations without performing database operations. */
public final class ProfilePersistenceAssembler {
    private ProfilePersistenceAssembler() {
    }

    public static Profile toDomainFromPersistence(ProfilePersistenceEntity entity) {
        if (entity == null) return null;
        return Profile.reconstitute(
                entity.getId(), entity.getAccountId(), entity.getProfileType(),
                new PersonName(entity.getName().getFirstName(), entity.getName().getLastName()),
                entity.getPhoneNumber());
    }

    public static ProfilePersistenceEntity toPersistenceFromDomain(Profile profile) {
        var entity = new ProfilePersistenceEntity();
        entity.setId(profile.getId());
        entity.setAccountId(profile.getAccountId());
        entity.setProfileType(profile.getProfileType());
        entity.setName(new PersonNamePersistenceEmbeddable(
                profile.getName().firstName(), profile.getName().lastName()));
        entity.setPhoneNumber(profile.getPhoneNumber());
        return entity;
    }
}
