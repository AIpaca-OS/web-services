package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ProfilePersistenceAssemblerTests {
    @Test
    void newProfileKeepsUnassignedIdentityUntilPersistenceReturnsOne() {
        var profile = new Profile(new CreateProfileCommand(
                new AccountId(101L), ProfileType.TUTOR, "Ana", "Perez", "987654321"));
        var entity = ProfilePersistenceAssembler.toPersistenceFromDomain(profile);
        assertNull(entity.getId());
        assertEquals(profile.getAccountId(), entity.getAccountId());
        entity.setId(7L);

        var restored = ProfilePersistenceAssembler.toDomainFromPersistence(entity);
        assertEquals(7L, restored.getId());
        assertNull(profile.getId());
        assertTrue(restored.domainEvents().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(ProfileType.class)
    void reconstitutionPreservesAllPersistedProfileInformation(ProfileType type) {
        var profile = Profile.reconstitute(7L, new AccountId(101L), type,
                new PersonName("Ana", "Perez"), "987654321");
        var entity = ProfilePersistenceAssembler.toPersistenceFromDomain(profile);
        var restored = ProfilePersistenceAssembler.toDomainFromPersistence(entity);

        assertEquals(profile.getId(), restored.getId());
        assertEquals(profile.getAccountId(), restored.getAccountId());
        assertEquals(profile.getProfileType(), restored.getProfileType());
        assertEquals(profile.getName(), restored.getName());
        assertEquals(profile.getPhoneNumber(), restored.getPhoneNumber());
        assertEquals("Ana", entity.getName().getFirstName());
        assertEquals("Perez", entity.getName().getLastName());
    }

    @Test
    void storedInvalidInformationCannotBypassDomainValidation() {
        var profile = Profile.reconstitute(7L, new AccountId(101L), ProfileType.DRIVER,
                new PersonName("Ana", "Perez"), "987654321");
        var entity = ProfilePersistenceAssembler.toPersistenceFromDomain(profile);
        entity.getName().setFirstName(" ");
        assertThrows(IllegalArgumentException.class,
                () -> ProfilePersistenceAssembler.toDomainFromPersistence(entity));
    }
}
