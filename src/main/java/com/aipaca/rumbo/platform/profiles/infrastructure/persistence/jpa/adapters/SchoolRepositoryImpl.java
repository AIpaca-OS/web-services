package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.domain.repositories.SchoolRepository;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.SchoolPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.SchoolPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Bridges the school domain port to Spring Data JPA through the persistence assembler. */
@Repository
public class SchoolRepositoryImpl implements SchoolRepository {
    private final SchoolPersistenceRepository schoolPersistenceRepository;

    public SchoolRepositoryImpl(SchoolPersistenceRepository schoolPersistenceRepository) {
        this.schoolPersistenceRepository = schoolPersistenceRepository;
    }

    @Override
    public Optional<School> findById(Long id) {
        return schoolPersistenceRepository.findById(id)
                .map(SchoolPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<School> findAll() {
        return schoolPersistenceRepository.findAll().stream()
                .map(SchoolPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public School save(School school) {
        var savedEntity = schoolPersistenceRepository.save(
                SchoolPersistenceAssembler.toPersistenceFromDomain(school));
        return SchoolPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
