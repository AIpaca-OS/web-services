package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.AuthorizeTutorStudentRelationshipCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.TutorStudentRelationshipPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.TutorStudentRelationshipPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.TutorStudentRelationshipPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorStudentRelationshipRepositoryImplTests {
    private static final Instant AUTHORIZED_AT = Instant.parse("2026-11-10T12:00:00Z");
    private static final Instant REVOKED_AT = AUTHORIZED_AT.plusSeconds(60);
    @Mock
    private TutorStudentRelationshipPersistenceRepository persistenceRepository;
    private TutorStudentRelationshipRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new TutorStudentRelationshipRepositoryImpl(persistenceRepository);
    }

    private TutorStudentRelationship existingRelationship(Long id, Long tutor, Long student, boolean revoked) {
        return TutorStudentRelationship.reconstitute(id, tutor, student, 4L, RelationshipType.LEGAL_GUARDIAN,
                revoked ? AuthorizationStatus.REVOKED : AuthorizationStatus.AUTHORIZED,
                AUTHORIZED_AT, revoked ? REVOKED_AT : null);
    }

    @Test
    void findByIdReconstitutesTheRelationship() {
        when(persistenceRepository.findById(1L)).thenReturn(Optional.of(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(existingRelationship(1L, 2L, 3L, false))));
        var relationship = repository.findById(1L).orElseThrow();
        assertEquals(1L, relationship.getId());
        assertEquals(2L, relationship.getTutorProfileId());
        assertEquals(3L, relationship.getStudentId());
        assertTrue(relationship.isAuthorized());
    }

    @Test
    void missingRelationshipIdentityRemainsAbsent() {
        when(persistenceRepository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(repository.findById(1L).isEmpty());
    }

    @Test
    void pairLookupDoesNotReauthorizeARevokedRelationship() {
        when(persistenceRepository.findByTutorProfileIdAndStudentId(2L, 3L)).thenReturn(Optional.of(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(existingRelationship(1L, 2L, 3L, true))));
        var relationship = repository.findByTutorProfileIdAndStudentId(2L, 3L).orElseThrow();
        assertEquals(1L, relationship.getId());
        assertEquals(REVOKED_AT, relationship.getRevokedAt());
        assertFalse(relationship.isAuthorized());
        verify(persistenceRepository).findByTutorProfileIdAndStudentId(2L, 3L);
        verify(persistenceRepository, never()).findById(any());
    }

    @Test
    void missingPairRemainsAbsent() {
        when(persistenceRepository.findByTutorProfileIdAndStudentId(2L, 3L)).thenReturn(Optional.empty());
        assertTrue(repository.findByTutorProfileIdAndStudentId(2L, 3L).isEmpty());
    }

    @Test
    void tutorLookupKeepsAuthorizedAndRevokedStatesForTheSuppliedTutor() {
        when(persistenceRepository.findAllByTutorProfileId(2L)).thenReturn(List.of(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(existingRelationship(1L, 2L, 3L, false)),
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(existingRelationship(6L, 2L, 5L, true))));
        var relationships = repository.findAllByTutorProfileId(2L);
        assertEquals(List.of(3L, 5L), relationships.stream().map(TutorStudentRelationship::getStudentId).toList());
        assertTrue(relationships.getFirst().isAuthorized());
        assertFalse(relationships.getLast().isAuthorized());
        verify(persistenceRepository, never()).findAll();
    }

    @Test
    void studentLookupKeepsAuthorizedAndRevokedStatesForTheSuppliedStudent() {
        when(persistenceRepository.findAllByStudentId(3L)).thenReturn(List.of(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(existingRelationship(1L, 2L, 3L, false)),
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(existingRelationship(6L, 5L, 3L, true))));
        var relationships = repository.findAllByStudentId(3L);
        assertEquals(List.of(2L, 5L), relationships.stream().map(TutorStudentRelationship::getTutorProfileId).toList());
        assertFalse(relationships.getLast().isAuthorized());
        verify(persistenceRepository, never()).findAll();
    }

    @Test
    void emptyScopedLookupsDoNotFallBackToGlobalQueries() {
        when(persistenceRepository.findAllByTutorProfileId(2L)).thenReturn(List.of());
        when(persistenceRepository.findAllByStudentId(3L)).thenReturn(List.of());
        assertTrue(repository.findAllByTutorProfileId(2L).isEmpty());
        assertTrue(repository.findAllByStudentId(3L).isEmpty());
        verify(persistenceRepository, never()).findAll();
    }

    @Test
    void saveNewRelationshipUsesTheEntityReturnedByJpa() {
        var relationship = new TutorStudentRelationship(new AuthorizeTutorStudentRelationshipCommand(
                3L, 2L, 4L, RelationshipType.LEGAL_GUARDIAN), AUTHORIZED_AT);
        var savedEntity = TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(
                existingRelationship(1L, 2L, 3L, false));
        when(persistenceRepository.save(any(TutorStudentRelationshipPersistenceEntity.class))).thenAnswer(invocation -> {
            TutorStudentRelationshipPersistenceEntity input = invocation.getArgument(0);
            assertNull(input.getId());
            assertEquals(2L, input.getTutorProfileId());
            assertEquals(3L, input.getStudentId());
            assertEquals(4L, input.getAuthorizedByProfileId());
            return savedEntity;
        });
        assertEquals(1L, repository.save(relationship).getId());
        assertNull(relationship.getId());
    }

    @Test
    void revocationAndReauthorizationSaveTheSameIdentity() {
        var relationship = existingRelationship(1L, 2L, 3L, true);
        when(persistenceRepository.save(any(TutorStudentRelationshipPersistenceEntity.class))).thenAnswer(invocation -> {
            TutorStudentRelationshipPersistenceEntity input = invocation.getArgument(0);
            assertEquals(1L, input.getId());
            return input;
        });
        var revoked = repository.save(relationship);
        assertFalse(revoked.isAuthorized());
        assertEquals(REVOKED_AT, revoked.getRevokedAt());
        revoked.authorize(REVOKED_AT.plusSeconds(60));
        var authorized = repository.save(revoked);
        assertEquals(1L, authorized.getId());
        assertTrue(authorized.isAuthorized());
        assertNull(authorized.getRevokedAt());
        verify(persistenceRepository, times(2)).save(any(TutorStudentRelationshipPersistenceEntity.class));
    }

    @Test
    void pairUniquenessFailureIsPropagated() {
        var failure = new DataIntegrityViolationException("uk_tutor_student_relationships_tutor_student");
        when(persistenceRepository.save(any(TutorStudentRelationshipPersistenceEntity.class))).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(existingRelationship(1L, 2L, 3L, false))));
    }
}
