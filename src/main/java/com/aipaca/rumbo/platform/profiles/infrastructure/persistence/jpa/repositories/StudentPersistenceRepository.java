package com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.aipaca.rumbo.platform.profiles.infrastructure.persistence.jpa.entities.StudentPersistenceEntity;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AuthorizationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Loads the full aggregate so Domain can enforce tutor invariants. */
@Repository
public interface StudentPersistenceRepository extends JpaRepository<StudentPersistenceEntity, Long> {
    @Override
    @EntityGraph(attributePaths = "tutorRelationships")
    Optional<StudentPersistenceEntity> findById(Long id);

    @EntityGraph(attributePaths = "tutorRelationships")
    @Query("SELECT DISTINCT s FROM StudentPersistenceEntity s WHERE s.id IN :ids")
    List<StudentPersistenceEntity> findAllWithRelationshipsByIdIn(@Param("ids") Collection<Long> ids);

    @EntityGraph(attributePaths = "tutorRelationships")
    @Query("SELECT DISTINCT s FROM StudentPersistenceEntity s JOIN s.tutorRelationships r " +
           "WHERE r.tutorProfileId = :tutorId AND r.authorizationStatus = :authorizationStatus")
    List<StudentPersistenceEntity> findAllByTutorProfileIdAndAuthorizationStatus(
            @Param("tutorId") Long tutorProfileId,
            @Param("authorizationStatus") AuthorizationStatus authorizationStatus);

    default List<StudentPersistenceEntity> findAllByAuthorizedTutorProfileId(Long tutorProfileId) {
        return findAllByTutorProfileIdAndAuthorizationStatus(tutorProfileId, AuthorizationStatus.AUTHORIZED);
    }

    @EntityGraph(attributePaths = "tutorRelationships")
    @Query("SELECT DISTINCT s FROM StudentPersistenceEntity s JOIN s.tutorRelationships r " +
           "WHERE r.tutorProfileId = :tutorId")
    List<StudentPersistenceEntity> findAllByTutorProfileId(@Param("tutorId") Long tutorProfileId);
}
