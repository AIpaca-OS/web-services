package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.assemblers.StudentPersistenceAssembler;
import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories.StudentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Implements the student port without granting access or changing student lifecycle rules. */
@Repository
public class StudentRepositoryImpl implements StudentRepository {
    private final StudentPersistenceRepository studentPersistenceRepository;

    public StudentRepositoryImpl(StudentPersistenceRepository studentPersistenceRepository) {
        this.studentPersistenceRepository = studentPersistenceRepository;
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentPersistenceRepository.findById(id)
                .map(StudentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Student> findAllById(Collection<Long> studentIds) {
        if (studentIds.isEmpty()) return List.of();
        return studentPersistenceRepository.findAllById(studentIds).stream()
                .map(StudentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Student save(Student student) {
        var savedEntity = studentPersistenceRepository.save(
                StudentPersistenceAssembler.toPersistenceFromDomain(student));
        return StudentPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
