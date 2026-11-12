package com.aipaca.rumbo.platform.profiles.application.queryservices;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllStudentsByTutorIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllTutorStudentRelationshipsByStudentIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllTutorStudentRelationshipsByTutorIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetStudentByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface StudentQueryService {
    java.util.Optional<Student> handle(GetStudentByIdQuery query);
    java.util.List<Student> handle(GetAllStudentsByTutorIdQuery query);
    java.util.List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByTutorIdQuery query);
    java.util.List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByStudentIdQuery query);
    java.util.Optional<TutorStudentRelationship> handle(GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery query);
}
