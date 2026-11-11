package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.TutorStudentRelationshipPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Spring Data queries include revoked relationships; access decisions belong to application services. */
@Repository
public interface TutorStudentRelationshipPersistenceRepository
        extends JpaRepository<TutorStudentRelationshipPersistenceEntity, Long> {
    Optional<TutorStudentRelationshipPersistenceEntity> findByTutorProfileIdAndStudentId(Long tutorProfileId, Long studentId);

    List<TutorStudentRelationshipPersistenceEntity> findAllByTutorProfileId(Long tutorProfileId);

    List<TutorStudentRelationshipPersistenceEntity> findAllByStudentId(Long studentId);
}
