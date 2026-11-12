package com.aipaca.rumbo.incidents.application.queryservices;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetAllIncidentsByTripIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetIncidentByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Incident Query Service
 */
public interface IncidentQueryService {
    Optional<Incident> handle(GetIncidentByIdQuery query);

    List<Incident> handle(GetAllIncidentsByTripIdQuery query);
}
