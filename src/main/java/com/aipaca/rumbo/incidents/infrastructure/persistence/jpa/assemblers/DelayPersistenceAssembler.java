package com.aipaca.rumbo.incidents.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities.DelayPersistenceEntity;

/**
 * Static assembler between delay domain and persistence representations.
 */
public final class DelayPersistenceAssembler {

    private DelayPersistenceAssembler() {
    }

    public static Delay toDomainFromPersistence(DelayPersistenceEntity entity) {
        if (entity == null) return null;
        return Delay.reconstitute(entity.getId(), entity.getTripId(), entity.getReportedByProfileId(),
                entity.getCause(), entity.getEstimatedMinutes(), entity.getStatus(), entity.getPreviousDelayId(),
                entity.getReportedAt());
    }

    public static DelayPersistenceEntity toPersistenceFromDomain(Delay delay) {
        if (delay == null) return null;
        var entity = new DelayPersistenceEntity();
        // Only set ID if the delay is being updated; for new delays JPA generates it
        if (delay.getId() != null) {
            entity.setId(delay.getId());
        }
        entity.setTripId(delay.getTripId());
        entity.setReportedByProfileId(delay.getReportedByProfileId());
        entity.setCause(delay.getCause());
        entity.setEstimatedMinutes(delay.getEstimatedMinutes());
        entity.setStatus(delay.getStatus());
        entity.setPreviousDelayId(delay.getPreviousDelayId());
        entity.setReportedAt(delay.getReportedAt());
        return entity;
    }
}
