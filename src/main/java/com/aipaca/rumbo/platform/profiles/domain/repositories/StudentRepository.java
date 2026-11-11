package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Student repository port. Application services must authorize access before returning student data. */
public interface StudentRepository {
    Optional<Student> findById(Long id);

    /**
     * Loads existing students for the supplied identities, for example after selecting
     * authorized tutor relationships. An empty collection returns an empty list.
     */
    List<Student> findAllById(Collection<Long> studentIds);

    Student save(Student student);
}
