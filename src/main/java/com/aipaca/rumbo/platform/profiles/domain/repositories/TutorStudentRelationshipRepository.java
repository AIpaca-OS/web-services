package com.aipaca.rumbo.platform.profiles.domain.repositories;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.TutorStudentRelationship;

import java.util.List;
import java.util.Optional;

/** Tutor-student relationship repository port, including revoked relationships. */
public interface TutorStudentRelationshipRepository {
    Optional<TutorStudentRelationship> findById(Long id);

    /** The caller must check isAuthorized() before granting access to student data. */
    Optional<TutorStudentRelationship> findByTutorProfileIdAndStudentId(Long tutorProfileId, Long studentId);

    List<TutorStudentRelationship> findAllByTutorProfileId(Long tutorProfileId);

    List<TutorStudentRelationship> findAllByStudentId(Long studentId);

    TutorStudentRelationship save(TutorStudentRelationship relationship);
}
