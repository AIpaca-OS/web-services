package com.aipaca.rumbo.incidents.domain.model.queries;

/**
 * Get Incident By ID Query
 *
 * @param incidentId incident id
 */
public record GetIncidentByIdQuery(Long incidentId) {
    /**
     * Compact constructor for GetIncidentByIdQuery.
     *
     * @throws IllegalArgumentException if any value is invalid.
     */
    public GetIncidentByIdQuery {
        if (incidentId == null || incidentId < 1) {
            throw new IllegalArgumentException("Incident id cannot be null or less than 1");
        }
    }
}
