package com.aipaca.rumbo.platform.incidents.application.internal.queryservices;

import com.aipaca.rumbo.platform.incidents.application.queryservices.IncidentQueryService;
import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetAllIncidentsByTripIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetIncidentByIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.domain.repositories.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Incident Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class IncidentQueryServiceImpl implements IncidentQueryService {
    private final IncidentRepository incidentRepository;

    /**
     * Constructor
     *
     * @param incidentRepository The {@link IncidentRepository} instance
     */
    public IncidentQueryServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Incident> handle(GetIncidentByIdQuery query) {
        return incidentRepository.findById(query.incidentId());
    }

    // inherited javadoc
    @Override
    public List<Incident> handle(GetAllIncidentsByTripIdQuery query) {
        return incidentRepository.findAllByTripId(new TripId(query.tripId()));
    }
}
