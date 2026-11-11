package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.StudentPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.StudentPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.StudentPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentRepositoryImplTests {
    private static final Instant AT = Instant.parse("2026-11-10T12:00:00Z");
    @Mock StudentPersistenceRepository persistenceRepository;
    StudentRepositoryImpl repository;
    @BeforeEach void setup() { repository = new StudentRepositoryImpl(persistenceRepository); }
    private Student student() {
        return new Student(new RegisterStudentCommand(1L, "Ana", "Perez", LocalDate.of(2016, 3, 10),
                2L, RelationshipType.MOTHER), AT);
    }
    private StudentPersistenceEntity stored(long id) {
        var entity = StudentPersistenceAssembler.toPersistenceFromDomain(student());
        entity.setId(id);
        entity.setVersion(0L);
        entity.getTutorRelationships().getFirst().setId(11L);
        return entity;
    }

    @Test void idLookupReturnsEntireAggregate() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.of(stored(7)));
        var actual = repository.findById(7L).orElseThrow();
        assertEquals(7L, actual.getId());
        assertEquals(1, actual.getTutorRelationships().size());
        assertTrue(actual.hasAuthorizedTutor(1L));
        assertEquals(0L, actual.getVersion());
        assertEquals(0L, actual.getAuthorizationRevision());
    }

    @Test void missingStudentIsAbsent() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.empty());
        assertTrue(repository.findById(7L).isEmpty());
    }

    @Test void emptyIdCollectionNeverTriggersGlobalQuery() {
        assertTrue(repository.findAllById(List.of()).isEmpty());
        verifyNoInteractions(persistenceRepository);
    }

    @Test void batchLookupUsesAggregateGraphQuery() {
        when(persistenceRepository.findAllWithRelationshipsByIdIn(List.of(7L)))
                .thenReturn(List.of(stored(7)));
        assertEquals(1, repository.findAllById(List.of(7L)).size());
    }

    @Test void authorizedTutorLookupIsScoped() {
        when(persistenceRepository.findAllByAuthorizedTutorProfileId(1L)).thenReturn(List.of(stored(7)));
        assertEquals(1, repository.findAllByAuthorizedTutorProfileId(1L).size());
    }

    @Test void historyLookupIsScoped() {
        when(persistenceRepository.findAllByTutorProfileId(1L)).thenReturn(List.of(stored(7)));
        assertEquals(1, repository.findAllByTutorProfileId(1L).size());
    }

    @Test void saveCascadesChildrenAndReturnsIdentityAndVersion() {
        when(persistenceRepository.save(any(StudentPersistenceEntity.class))).thenAnswer(inv -> {
            StudentPersistenceEntity input = inv.getArgument(0);
            assertEquals(1, input.getTutorRelationships().size());
            assertSame(input, input.getTutorRelationships().getFirst().getStudent());
            input.setId(7L);
            input.setVersion(0L);
            input.getTutorRelationships().getFirst().setId(11L);
            return input;
        });
        var saved = repository.save(student());
        assertEquals(7L, saved.getId());
        assertEquals(0L, saved.getVersion());
        assertEquals(11L, saved.getTutorRelationships().getFirst().getId());
    }

    @Test void concurrencyConflictIsNotSwallowed() {
        var failure = new OptimisticLockingFailureException("Version conflict");
        when(persistenceRepository.save(any(StudentPersistenceEntity.class))).thenThrow(failure);
        assertSame(failure, assertThrows(OptimisticLockingFailureException.class,
                () -> repository.save(student())));
    }
}
