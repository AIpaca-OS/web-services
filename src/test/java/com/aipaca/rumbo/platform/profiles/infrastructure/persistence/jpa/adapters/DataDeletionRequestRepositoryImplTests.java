package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.DataDeletionRequest;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RequestStudentDataDeletionCommand;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.DataDeletionRequestPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.DataDeletionRequestPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.DataDeletionRequestPersistenceRepository;
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
class DataDeletionRequestRepositoryImplTests {
    @Mock DataDeletionRequestPersistenceRepository persistenceRepository;
    DataDeletionRequestRepositoryImpl repository;
    @BeforeEach void setup() { repository = new DataDeletionRequestRepositoryImpl(persistenceRepository); }
    private DataDeletionRequest request() {
        return new DataDeletionRequest(new RequestStudentDataDeletionCommand(7L, 2L),
                Instant.parse("2026-11-10T12:00:00Z"));
    }
    private DataDeletionRequestPersistenceEntity stored() {
        var entity = DataDeletionRequestPersistenceAssembler.toPersistenceFromDomain(request());
        entity.setId(10L);
        return entity;
    }
    @Test void lookupByIdKeepsIdentity() {
        when(persistenceRepository.findById(10L)).thenReturn(Optional.of(stored()));
        assertEquals(10L, repository.findById(10L).orElseThrow().getId());
    }
    @Test void missingIdStaysAbsent() {
        when(persistenceRepository.findById(10L)).thenReturn(Optional.empty());
        assertTrue(repository.findById(10L).isEmpty());
    }
    @Test void requesterScopedListNeverCallsFindAll() {
        when(persistenceRepository.findAllByRequestedByProfileId(2L)).thenReturn(List.of(stored()));
        assertEquals(1, repository.findAllByRequestedByProfileId(2L).size());
        verify(persistenceRepository, never()).findAll();
    }
    @Test void saveReturnsPersistenceAssignedId() {
        when(persistenceRepository.save(any(DataDeletionRequestPersistenceEntity.class))).thenAnswer(inv -> {
            DataDeletionRequestPersistenceEntity entity = inv.getArgument(0);
            assertNull(entity.getId());
            entity.setId(10L);
            return entity;
        });
        assertEquals(10L, repository.save(request()).getId());
    }
    @Test void saveFailuresAreNotHidden() {
        var failure = new DataIntegrityViolationException("Integrity failure");
        when(persistenceRepository.save(any(DataDeletionRequestPersistenceEntity.class))).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class, () -> repository.save(request())));
    }
}
