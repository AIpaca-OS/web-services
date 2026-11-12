package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Student repository port. Tutor authorizations are persisted only as children of the Student aggregate.
 */
public interface StudentRepository {
    Optional<Student> findById(Long id);

    List<Student> findAllById(Collection<Long> studentIds);

    /**
     * Finds the students the tutor is currently authorized for.
     *
     * @param tutorProfileId The tutor profile id.
     * @return The students with an active authorization of the tutor.
     */
    List<Student> findAllByAuthorizedTutorProfileId(Long tutorProfileId);

    /**
     * Finds the students that have any relationship with the tutor, including revoked history.
     *
     * @param tutorProfileId The tutor profile id.
     * @return The students related to the tutor.
     */
    List<Student> findAllByTutorProfileId(Long tutorProfileId);

    Student save(Student student);
}
