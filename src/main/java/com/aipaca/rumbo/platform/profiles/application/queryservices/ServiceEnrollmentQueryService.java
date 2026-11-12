package com.aipaca.rumbo.platform.profiles.application.queryservices;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllServiceEnrollmentsByStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllStudentsByDriverIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface ServiceEnrollmentQueryService {
    java.util.List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByDriverIdQuery query);
    java.util.List<ServiceEnrollment> handle(GetAllServiceEnrollmentsByStudentIdQuery query);
    java.util.List<Student> handle(GetAllStudentsByDriverIdQuery query);
}
