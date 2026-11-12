package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileRepositoryImplTests {
    @Mock
    private ProfilePersistenceRepository persistenceRepository;
    private ProfileRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ProfileRepositoryImpl(persistenceRepository);
    }

    private Profile existingProfile() {
        return Profile.reconstitute(7L, new AccountId(101L), ProfileType.TUTOR,
                new PersonName("Ana", "Perez"), "987654321");
    }

    @Test
    void findByIdReturnsReconstitutedProfile() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.of(
                ProfilePersistenceAssembler.toPersistenceFromDomain(existingProfile())));
        var profile = repository.findById(7L).orElseThrow();
        assertEquals(7L, profile.getId());
        assertEquals(new AccountId(101L), profile.getAccountId());
    }

    @Test
    void missingIdentityRemainsAbsent() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.empty());
        assertTrue(repository.findById(7L).isEmpty());
    }

    @Test
    void findByAccountUsesExternalAccountIdentityInsteadOfProfileIdentity() {
        var accountId = new AccountId(101L);
        when(persistenceRepository.findByAccountId(accountId)).thenReturn(Optional.of(
                ProfilePersistenceAssembler.toPersistenceFromDomain(existingProfile())));
        assertEquals(7L, repository.findByAccountId(accountId).orElseThrow().getId());
        verify(persistenceRepository, never()).findById(any());
    }

    @Test
    void missingAccountRemainsAbsent() {
        var accountId = new AccountId(101L);
        when(persistenceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
        assertTrue(repository.findByAccountId(accountId).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 2L})
    void accountExistenceUsesTheCount(Long count) {
        var accountId = new AccountId(101L);
        when(persistenceRepository.countByAccountId(accountId)).thenReturn(count);
        assertEquals(count > 0, repository.existsByAccountId(accountId));
    }

    @Test
    void saveNewProfileUsesIdentityAndStateFromReturnedEntity() {
        var profile = new Profile(new CreateProfileCommand(new AccountId(101L),
                ProfileType.TUTOR, "Ana", "Perez", "987654321"));
        var savedEntity = ProfilePersistenceAssembler.toPersistenceFromDomain(existingProfile());
        when(persistenceRepository.save(any(ProfilePersistenceEntity.class))).thenAnswer(invocation -> {
            ProfilePersistenceEntity input = invocation.getArgument(0);
            assertNull(input.getId());
            assertEquals(profile.getAccountId(), input.getAccountId());
            assertEquals(profile.getPhoneNumber(), input.getPhoneNumber());
            return savedEntity;
        });

        assertEquals(7L, repository.save(profile).getId());
        assertNull(profile.getId());
    }

    @Test
    void saveExistingProfilePreservesItsIdentityAndUpdatedContact() {
        var profile = existingProfile();
        profile.updateContact("999888777");
        when(persistenceRepository.save(any(ProfilePersistenceEntity.class))).thenAnswer(invocation -> {
            ProfilePersistenceEntity input = invocation.getArgument(0);
            assertEquals(7L, input.getId());
            assertEquals("999888777", input.getPhoneNumber());
            return input;
        });

        var saved = repository.save(profile);
        assertEquals(7L, saved.getId());
        assertEquals("999888777", saved.getPhoneNumber());
    }

    @Test
    void constraintViolationIsPropagatedForTheApplicationToHandle() {
        var failure = new DataIntegrityViolationException("uk_profiles_account_id");
        when(persistenceRepository.save(any(ProfilePersistenceEntity.class))).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(existingProfile())));
    }
}
