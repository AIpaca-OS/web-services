package com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.incidents.domain.model.aggregates.Incident;
import com.aipaca.rumbo.platform.incidents.domain.model.entities.IncidentAcknowledgement;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities.IncidentAcknowledgementPersistenceEntity;
import com.aipaca.rumbo.platform.incidents.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;

import java.util.ArrayList;

/**
 * Static assembler between incident domain and persistence representations.
 */
public final class IncidentPersistenceAssembler {

    private IncidentPersistenceAssembler() {
    }

    public static Incident toDomainFromPersistence(IncidentPersistenceEntity entity) {
        if (entity == null) return null;
        var acknowledgements = entity.getAcknowledgements().stream()
                .map(acknowledgement -> new IncidentAcknowledgement(acknowledgement.getId(),
                        acknowledgement.getTutorProfileId(), acknowledgement.getAcknowledgedAt()))
                .toList();
        return Incident.reconstitute(entity.getId(), entity.getTripId(), entity.getReportedByProfileId(),
                entity.getStudentId(), entity.getCategory(), entity.getDescription(), entity.getStatus(),
                entity.getResolution(), entity.getReportedAt(), entity.getResolvedAt(), acknowledgements);
    }

    public static IncidentPersistenceEntity toPersistenceFromDomain(Incident incident) {
        if (incident == null) return null;
        var entity = new IncidentPersistenceEntity();
        // Only set ID if the incident is being updated; for new incidents JPA generates it
        if (incident.getId() != null) {
            entity.setId(incident.getId());
        }
        entity.setTripId(incident.getTripId());
        entity.setReportedByProfileId(incident.getReportedByProfileId());
        entity.setStudentId(incident.getStudentId());
        entity.setCategory(incident.getCategory());
        entity.setDescription(incident.getDescription());
        entity.setStatus(incident.getStatus());
        entity.setResolution(incident.getResolution());
        entity.setReportedAt(incident.getReportedAt());
        entity.setResolvedAt(incident.getResolvedAt());

        var acknowledgements = new ArrayList<IncidentAcknowledgementPersistenceEntity>();
        for (var acknowledgement : incident.getAcknowledgements()) {
            var persistenceAcknowledgement = new IncidentAcknowledgementPersistenceEntity();
            if (acknowledgement.getId() != null) {
                persistenceAcknowledgement.setId(acknowledgement.getId());
            }
            persistenceAcknowledgement.setIncident(entity);
            persistenceAcknowledgement.setTutorProfileId(acknowledgement.getTutorProfileId());
            persistenceAcknowledgement.setAcknowledgedAt(acknowledgement.getAcknowledgedAt());
            acknowledgements.add(persistenceAcknowledgement);
        }
        entity.setAcknowledgements(acknowledgements);
        return entity;
    }
}
