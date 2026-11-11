package com.aipaca.rumbo.platform.profiles.application.queryservices;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllDataDeletionRequestsByRequestedByProfileIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetDataDeletionRequestByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
/** Learning Center Platform CQRS service contract for Rumbo. */
public interface DataDeletionRequestQueryService {
    java.util.Optional<DataDeletionRequest> handle(GetDataDeletionRequestByIdQuery query);
    java.util.List<DataDeletionRequest> handle(GetAllDataDeletionRequestsByRequestedByProfileIdQuery query);
}
