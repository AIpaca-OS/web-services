package com.aipaca.rumbo.incidents.interfaces.rest.transform;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.IncidentAcknowledgementResource;
import com.aipaca.rumbo.platform.incidents.interfaces.rest.resources.IncidentResource;

/**
 * Assembler to convert an Incident entity to an IncidentResource.
 */
public class IncidentResourceFromEntityAssembler {
    /**
     * Converts an Incident entity to an IncidentResource.
     *
     * @param entity The {@link Incident} entity to convert.
     * @return The {@link IncidentResource} resource.
     */
    public static IncidentResource toResourceFromEntity(Incident entity) {
        var acknowledgements = entity.getAcknowledgements().stream()
                .map(acknowledgement -> new IncidentAcknowledgementResource(acknowledgement.getId(),
                        acknowledgement.getTutorProfileIdValue(), acknowledgement.getAcknowledgedAt()))
                .toList();
        return new IncidentResource(entity.getId(), entity.getTripIdValue(), entity.getReportedByProfileIdValue(),
                entity.getStudentIdValue(), entity.getCategory(), entity.getDescription(), entity.getStatus(),
                entity.getResolution(), entity.getReportedAt(), entity.getResolvedAt(), acknowledgements);
    }
}
