package com.aipaca.rumbo.platform.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Delay;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.DelayResource;

/**
 * Assembler to convert a Delay entity to a DelayResource.
 */
public class DelayResourceFromEntityAssembler {
    /**
     * Converts a Delay entity to a DelayResource.
     *
     * @param entity The {@link Delay} entity to convert.
     * @return The {@link DelayResource} resource.
     */
    public static DelayResource toResourceFromEntity(Delay entity) {
        return new DelayResource(entity.getId(), entity.getTripIdValue(), entity.getReportedByProfileIdValue(),
                entity.getCause(), entity.getEstimatedMinutes(), entity.getStatus(), entity.getPreviousDelayId(),
                entity.getReportedAt());
    }
}
