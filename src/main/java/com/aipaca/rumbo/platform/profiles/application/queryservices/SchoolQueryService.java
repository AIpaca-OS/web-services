package com.aipaca.rumbo.platform.profiles.application.queryservices;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllSchoolsQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetSchoolByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * School Query Service
 */
public interface SchoolQueryService {
    /**
     * Handle Get All Schools Query
     *
     * @param query The {@link GetAllSchoolsQuery} Query
     * @return A list of {@link School} instances
     */
    List<School> handle(GetAllSchoolsQuery query);

    /**
     * Handle Get School By ID Query
     *
     * @param query The {@link GetSchoolByIdQuery} Query
     * @return A {@link School} instance if found, otherwise empty
     */
    Optional<School> handle(GetSchoolByIdQuery query);
}
