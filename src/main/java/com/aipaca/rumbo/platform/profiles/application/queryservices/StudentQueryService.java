package com.aipaca.rumbo.platform.profiles.application.queryservices;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.entities.TutorStudentRelationship;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllStudentsByTutorIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllTutorStudentRelationshipsByStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllTutorStudentRelationshipsByTutorIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetStudentByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Student Query Service
 */
public interface StudentQueryService {
    /**
     * Handle Get Student By ID Query
     *
     * @param query The {@link GetStudentByIdQuery} Query
     * @return A {@link Student} instance if found, otherwise empty
     */
    Optional<Student> handle(GetStudentByIdQuery query);

    /**
     * Handle Get All Students By Tutor ID Query
     *
     * @param query The {@link GetAllStudentsByTutorIdQuery} Query
     * @return A list of {@link Student} instances
     */
    List<Student> handle(GetAllStudentsByTutorIdQuery query);

    /**
     * Handle Get All Tutor Student Relationships By Tutor ID Query
     *
     * @param query The {@link GetAllTutorStudentRelationshipsByTutorIdQuery} Query
     * @return A list of {@link TutorStudentRelationship} instances
     */
    List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByTutorIdQuery query);

    /**
     * Handle Get All Tutor Student Relationships By Student ID Query
     *
     * @param query The {@link GetAllTutorStudentRelationshipsByStudentIdQuery} Query
     * @return A list of {@link TutorStudentRelationship} instances
     */
    List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByStudentIdQuery query);

    /**
     * Handle Get Active Tutor Student Relationship By Tutor ID And Student ID Query
     *
     * @param query The {@link GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery} Query
     * @return A {@link TutorStudentRelationship} instance if found, otherwise empty
     */
    Optional<TutorStudentRelationship> handle(GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery query);
}
