package com.aipaca.rumbo.platform.profiles.application.queryservices;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllSchoolsQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetSchoolByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface SchoolQueryService {
    java.util.List<School> handle(GetAllSchoolsQuery query);
    java.util.Optional<School> handle(GetSchoolByIdQuery query);
}
