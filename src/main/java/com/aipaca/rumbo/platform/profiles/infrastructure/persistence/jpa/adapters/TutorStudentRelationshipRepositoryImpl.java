package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.repositories.TutorStudentRelationshipRepository;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.TutorStudentRelationshipPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.TutorStudentRelationshipPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Implements the relationship domain port through the persistence assembler and JPA repository. */
@Repository
public class TutorStudentRelationshipRepositoryImpl implements TutorStudentRelationshipRepository {
    private final TutorStudentRelationshipPersistenceRepository tutorStudentRelationshipPersistenceRepository;

    public TutorStudentRelationshipRepositoryImpl(
            TutorStudentRelationshipPersistenceRepository tutorStudentRelationshipPersistenceRepository
    ) {
        this.tutorStudentRelationshipPersistenceRepository = tutorStudentRelationshipPersistenceRepository;
    }

    @Override
    public Optional<TutorStudentRelationship> findById(Long id) {
        return tutorStudentRelationshipPersistenceRepository.findById(id)
                .map(TutorStudentRelationshipPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<TutorStudentRelationship> findByTutorProfileIdAndStudentId(Long tutorProfileId, Long studentId) {
        return tutorStudentRelationshipPersistenceRepository.findByTutorProfileIdAndStudentId(tutorProfileId, studentId)
                .map(TutorStudentRelationshipPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<TutorStudentRelationship> findAllByTutorProfileId(Long tutorProfileId) {
        return tutorStudentRelationshipPersistenceRepository.findAllByTutorProfileId(tutorProfileId).stream()
                .map(TutorStudentRelationshipPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<TutorStudentRelationship> findAllByStudentId(Long studentId) {
        return tutorStudentRelationshipPersistenceRepository.findAllByStudentId(studentId).stream()
                .map(TutorStudentRelationshipPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public TutorStudentRelationship save(TutorStudentRelationship relationship) {
        var savedEntity = tutorStudentRelationshipPersistenceRepository.save(
                TutorStudentRelationshipPersistenceAssembler.toPersistenceFromDomain(relationship));
        return TutorStudentRelationshipPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
