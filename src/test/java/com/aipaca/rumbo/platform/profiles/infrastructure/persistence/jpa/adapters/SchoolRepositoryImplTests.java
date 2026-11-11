package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.SchoolPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.SchoolPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.SchoolPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolRepositoryImplTests {
    @Mock
    private SchoolPersistenceRepository persistenceRepository;
    private SchoolRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new SchoolRepositoryImpl(persistenceRepository);
    }

    private School existingSchool(Long id) {
        return School.reconstitute(id, "Colegio San Martin", "Av. Los Olivos 123", "San Miguel");
    }

    @Test
    void findByIdReconstitutesTheStoredSchool() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.of(
                SchoolPersistenceAssembler.toPersistenceFromDomain(existingSchool(7L))));
        var school = repository.findById(7L).orElseThrow();
        assertEquals(7L, school.getId());
        assertEquals("Colegio San Martin", school.getName());
    }

    @Test
    void missingSchoolRemainsAbsent() {
        when(persistenceRepository.findById(7L)).thenReturn(Optional.empty());
        assertTrue(repository.findById(7L).isEmpty());
    }

    @Test
    void catalogLookupMapsAllReturnedSchools() {
        when(persistenceRepository.findAll()).thenReturn(List.of(
                SchoolPersistenceAssembler.toPersistenceFromDomain(existingSchool(7L)),
                SchoolPersistenceAssembler.toPersistenceFromDomain(existingSchool(8L))));
        var schools = repository.findAll();
        assertEquals(List.of(7L, 8L), schools.stream().map(School::getId).toList());
        assertTrue(schools.stream().allMatch(school -> school.getDistrict().equals("San Miguel")));
    }

    @Test
    void emptyCatalogReturnsAnEmptyList() {
        when(persistenceRepository.findAll()).thenReturn(List.of());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void saveNewSchoolUsesTheEntityReturnedByJpa() {
        var school = new School("Colegio San Martin", "Av. Los Olivos 123", "San Miguel");
        var savedEntity = SchoolPersistenceAssembler.toPersistenceFromDomain(existingSchool(7L));
        when(persistenceRepository.save(any(SchoolPersistenceEntity.class))).thenAnswer(invocation -> {
            SchoolPersistenceEntity input = invocation.getArgument(0);
            assertNull(input.getId());
            assertEquals(school.getName(), input.getName());
            assertEquals(school.getAddress(), input.getAddress());
            assertEquals(school.getDistrict(), input.getDistrict());
            return savedEntity;
        });
        assertEquals(7L, repository.save(school).getId());
        assertNull(school.getId());
    }

    @Test
    void saveExistingSchoolPreservesItsIdentity() {
        var school = existingSchool(7L);
        when(persistenceRepository.save(any(SchoolPersistenceEntity.class))).thenAnswer(invocation -> {
            SchoolPersistenceEntity input = invocation.getArgument(0);
            assertEquals(7L, input.getId());
            return input;
        });
        var saved = repository.save(school);
        assertEquals(7L, saved.getId());
        assertEquals(school.getAddress(), saved.getAddress());
    }

    @Test
    void persistenceFailureIsPropagated() {
        var failure = new DataIntegrityViolationException("School constraint violation");
        when(persistenceRepository.save(any(SchoolPersistenceEntity.class))).thenThrow(failure);
        assertSame(failure, assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(existingSchool(7L))));
    }
}
