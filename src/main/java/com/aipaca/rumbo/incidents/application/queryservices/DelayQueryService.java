package com.aipaca.rumbo.incidents.application.queryservices;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetAllDelaysByTripIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetDelayByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Delay Query Service
 */
public interface DelayQueryService {
    Optional<Delay> handle(GetDelayByIdQuery query);

    List<Delay> handle(GetAllDelaysByTripIdQuery query);
}
