package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Aggregate repository; tutor links are persisted only as children of Student. */
public interface StudentRepository {
    Optional<Student> findById(Long id);
    List<Student> findAllById(Collection<Long> studentIds);
    /** Students currently accessible to this tutor (authorization checks still required by Application). */
    List<Student> findAllByAuthorizedTutorProfileId(Long tutorProfileId);
    /** Students containing a relationship with this tutor, including revoked history. */
    List<Student> findAllByTutorProfileId(Long tutorProfileId);
    Student save(Student student);
}
