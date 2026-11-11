package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.RelationshipType;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.StudentPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.StudentPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.StudentPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentRepositoryImplTests {
    private static final LocalDate BIRTH_DATE = LocalDate.of(2016, 3, 10);
    @Mock
    private StudentPersistenceRepository persistenceRepository;
    private StudentRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new StudentRepositoryImpl(persistenceRepository);
    }

    private Student existingStudent(Long id) {
        return Student.reconstitute(id, new PersonName("Ana", "Perez"),
                BIRTH_DATE, 2L, StudentStatus.ACTIVE, null);
    }

    @Test
    void findByIdReturnsReconstitutedStudent() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.of(
                StudentPersistenceAssembler.toPersistenceFromDomain(existingStudent(7L))));
        var student = repository.findById(7L).orElseThrow();
        assertEquals(7L, student.getId());
        assertEquals("Ana Perez", student.getFullName());
        assertEquals(BIRTH_DATE, student.getBirthDate());
    }

    @Test
    void missingIdentityRemainsAbsent() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.empty());
        assertTrue(repository.findById(7L).isEmpty());
    }

    @Test
    void batchLookupPassesOnlySuppliedIdentitiesAndMapsExistingStudents() {
        var ids = List.of(7L, 8L, 99L);
        when(persistenceRepository.findAllById(ids)).thenReturn(List.of(
                StudentPersistenceAssembler.toPersistenceFromDomain(existingStudent(7L)),
                StudentPersistenceAssembler.toPersistenceFromDomain(existingStudent(8L))));
        var students = repository.findAllById(ids);
        assertEquals(List.of(7L, 8L), students.stream().map(Student::getId).toList());
        verify(persistenceRepository).findAllById(ids);
        verify(persistenceRepository, never()).findAll();
    }

    @Test
    void emptyIdentityCollectionReturnsNothingWithoutQuerying() {
        assertTrue(repository.findAllById(List.of()).isEmpty());
        verifyNoInteractions(persistenceRepository);
    }

    @Test
    void batchWithNoMatchingStudentsDoesNotFallBackToGlobalLookup() {
        var ids = List.of(99L);
        when(persistenceRepository.findAllById(ids)).thenReturn(List.of());
        assertTrue(repository.findAllById(ids).isEmpty());
        verify(persistenceRepository, never()).findAll();
    }

    @Test
    void saveNewStudentReconstitutesFromReturnedEntity() {
        var student = new Student(new RegisterStudentCommand(
                1L, "Ana", "Perez", BIRTH_DATE, 2L, RelationshipType.MOTHER));
        var savedEntity = StudentPersistenceAssembler.toPersistenceFromDomain(existingStudent(7L));
        when(persistenceRepository.save(any(StudentPersistenceEntity.class))).thenAnswer(invocation -> {
            StudentPersistenceEntity input = invocation.getArgument(0);
            assertNull(input.getId());
            assertEquals(BIRTH_DATE, input.getBirthDate());
            assertEquals(2L, input.getSchoolId());
            return savedEntity;
        });
        assertEquals(7L, repository.save(student).getId());
        assertNull(student.getId());
    }

    @Test
    void saveUpdatedStudentPreservesIdentityAndAllowsClearingOptionalSchool() {
        var student = existingStudent(7L);
        student.updateInformation("Eva", "Lopez", BIRTH_DATE, null);
        when(persistenceRepository.save(any(StudentPersistenceEntity.class))).thenAnswer(invocation -> {
            StudentPersistenceEntity input = invocation.getArgument(0);
            assertEquals(7L, input.getId());
            assertNull(input.getSchoolId());
            return input;
        });
        var saved = repository.save(student);
        assertEquals(7L, saved.getId());
        assertEquals("Eva Lopez", saved.getFullName());
        assertNull(saved.getSchoolId());
    }

    @Test
    void persistenceFailureIsNotReportedAsSuccessfulSave() {
        var failure = new DataIntegrityViolationException("Student constraint violation");
        when(persistenceRepository.save(any(StudentPersistenceEntity.class))).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(existingStudent(7L))));
    }
}
