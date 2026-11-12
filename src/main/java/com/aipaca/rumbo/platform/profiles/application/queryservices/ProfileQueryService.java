package com.aipaca.rumbo.platform.profiles.application.queryservices;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByAccountIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByIdQuery;

import java.util.Optional;

/**
 * Profile Query Service
 */
public interface ProfileQueryService {
    /**
     * Handle Get Profile By ID Query
     *
     * @param query The {@link GetProfileByIdQuery} Query
     * @return A {@link Profile} instance if found, otherwise empty
     */
    Optional<Profile> handle(GetProfileByIdQuery query);

    /**
     * Handle Get Profile By Account ID Query
     *
     * @param query The {@link GetProfileByAccountIdQuery} Query
     * @return A {@link Profile} instance if found, otherwise empty
     */
    Optional<Profile> handle(GetProfileByAccountIdQuery query);
}
