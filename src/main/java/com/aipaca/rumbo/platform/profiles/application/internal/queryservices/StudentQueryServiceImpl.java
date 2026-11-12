package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;

import com.aipaca.rumbo.platform.profiles.application.queryservices.StudentQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.*;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Student Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class StudentQueryServiceImpl implements StudentQueryService {
    private final StudentRepository studentRepository;

    /**
     * Constructor
     *
     * @param studentRepository The {@link StudentRepository} instance
     */
    public StudentQueryServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Student> handle(GetStudentByIdQuery query) {
        return studentRepository.findById(query.studentId());
    }

    // inherited javadoc
    @Override
    public List<Student> handle(GetAllStudentsByTutorIdQuery query) {
        return studentRepository.findAllByAuthorizedTutorProfileId(query.tutorId()).stream()
                .filter(student -> student.hasAuthorizedTutor(query.tutorId()))
                .toList();
    }

    // inherited javadoc
    @Override
    public List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByStudentIdQuery query) {
        return studentRepository.findById(query.studentId())
                .map(Student::getTutorRelationships)
                .orElseGet(List::of);
    }

    // inherited javadoc
    @Override
    public List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByTutorIdQuery query) {
        return studentRepository.findAllByTutorProfileId(query.tutorId()).stream()
                .flatMap(student -> student.getTutorRelationships().stream()
                        .filter(relationship -> relationship.getTutorProfileId().equals(query.tutorId())))
                .toList();
    }

    // inherited javadoc
    @Override
    public Optional<TutorStudentRelationship> handle(GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery query) {
        return studentRepository.findById(query.studentId()).stream()
                .flatMap(student -> student.getTutorRelationships().stream())
                .filter(relationship -> relationship.getTutorProfileId().equals(query.tutorId())
                        && relationship.isAuthorized())
                .findFirst();
    }
}
