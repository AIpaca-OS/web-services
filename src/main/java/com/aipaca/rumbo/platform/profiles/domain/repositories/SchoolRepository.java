package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;

import java.util.List;
import java.util.Optional;

/** School catalog repository port. */
public interface SchoolRepository {
    Optional<School> findById(Long id);

    List<School> findAll();

    School save(School school);
}
