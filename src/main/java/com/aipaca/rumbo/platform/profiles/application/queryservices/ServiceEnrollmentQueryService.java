package com.aipaca.rumbo.platform.profiles.application.queryservices;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.ServiceEnrollment;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllStudentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetServiceEnrollmentByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service Enrollment Query Service
 */
public interface ServiceEnrollmentQueryService {
    /**
     * Handle Get Service Enrollment By ID Query
     *
     * @param query The {@link GetServiceEnrollmentByIdQuery} Query
     * @return A {@link ServiceEnrollment} instance if found, otherwise empty
     */
    Optional<ServiceEnrollment> handle(GetServiceEnrollmentByIdQuery query);

    /**
     * Handle Get All Service Enrollments By Driver ID Query
     *
     * @param query The {@link GetAllServiceEnrollmentsByDriverIdQuery} Query
     * @return A list of {@link ServiceEnrollment} instances
     */
    List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByDriverIdQuery query);

    /**
     * Handle Get All Service Enrollments By Student ID Query
     *
     * @param query The {@link GetAllServiceEnrollmentsByStudentIdQuery} Query
     * @return A list of {@link ServiceEnrollment} instances
     */
    List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByStudentIdQuery query);

    /**
     * Handle Get All Students By Driver ID Query: active students with a pending or accepted enrollment
     *
     * @param query The {@link GetAllStudentsByDriverIdQuery} Query
     * @return A list of {@link Student} instances
     */
    List<Student> handle(GetAllStudentsByDriverIdQuery query);
}
