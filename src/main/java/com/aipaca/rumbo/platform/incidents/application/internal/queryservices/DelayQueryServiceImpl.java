package com.aipaca.rumbo.platform.incidents.application.internal.queryservices;

import com.aipaca.rumbo.platform.incidents.application.queryservices.DelayQueryService;
import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetAllDelaysByTripIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.queries.GetDelayByIdQuery;
import com.aipaca.rumbo.platform.incidents.domain.model.valueobjects.TripId;
import com.aipaca.rumbo.platform.incidents.domain.repositories.DelayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Delay Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class DelayQueryServiceImpl implements DelayQueryService {
    private final DelayRepository delayRepository;

    /**
     * Constructor
     *
     * @param delayRepository The {@link DelayRepository} instance
     */
    public DelayQueryServiceImpl(DelayRepository delayRepository) {
        this.delayRepository = delayRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Delay> handle(GetDelayByIdQuery query) {
        return delayRepository.findById(query.delayId());
    }

    // inherited javadoc
    @Override
    public List<Delay> handle(GetAllDelaysByTripIdQuery query) {
        return delayRepository.findAllByTripId(new TripId(query.tripId()));
    }
}
