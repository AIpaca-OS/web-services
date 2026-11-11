package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.StudentPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.StudentPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Only Student is persisted as the aggregate root; authorization children cascade through JPA. */
@Repository
public class StudentRepositoryImpl implements StudentRepository {
    private final StudentPersistenceRepository studentPersistenceRepository;

    public StudentRepositoryImpl(StudentPersistenceRepository studentPersistenceRepository) {
        this.studentPersistenceRepository = studentPersistenceRepository;
    }

    @Override public Optional<Student> findById(Long id) {
        return studentPersistenceRepository.findById(id).map(StudentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override public List<Student> findAllById(Collection<Long> studentIds) {
        if (studentIds.isEmpty()) return List.of();
        return studentPersistenceRepository.findAllWithRelationshipsByIdIn(studentIds).stream()
                .map(StudentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override public List<Student> findAllByAuthorizedTutorProfileId(Long tutorProfileId) {
        return studentPersistenceRepository.findAllByAuthorizedTutorProfileId(tutorProfileId).stream()
                .map(StudentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override public List<Student> findAllByTutorProfileId(Long tutorProfileId) {
        return studentPersistenceRepository.findAllByTutorProfileId(tutorProfileId).stream()
                .map(StudentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override public Student save(Student student) {
        var savedEntity = studentPersistenceRepository.save(
                StudentPersistenceAssembler.toPersistenceFromDomain(student));
        return StudentPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
