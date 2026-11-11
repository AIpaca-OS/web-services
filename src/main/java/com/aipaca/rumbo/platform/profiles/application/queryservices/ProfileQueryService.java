package com.aipaca.rumbo.platform.profiles.application.queryservices;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByAccountIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface ProfileQueryService {
    java.util.Optional<Profile> handle(GetProfileByIdQuery query);
    java.util.Optional<Profile> handle(GetProfileByAccountIdQuery query);
}
