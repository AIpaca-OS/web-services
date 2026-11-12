package com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.assemblers;

import com.aipaca.rumbo.platform.trips.domain.model.aggregates.Trip;
import com.aipaca.rumbo.platform.trips.domain.model.entities.TripPassenger;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.entities.TripPassengerPersistenceEntity;
import com.aipaca.rumbo.platform.trips.infrastructure.persistence.jpa.entities.TripPersistenceEntity;

import java.util.ArrayList;

/**
 * Static assembler between trip domain and persistence representations.
 */
public final class TripPersistenceAssembler {

    private TripPersistenceAssembler() {
    }

    public static Trip toDomainFromPersistence(TripPersistenceEntity entity) {
        if (entity == null) return null;
        var passengers = entity.getPassengers().stream()
                .map(passenger -> new TripPassenger(passenger.getId(), passenger.getStudentId(),
                        passenger.getStopId(), passenger.getStopPosition(), passenger.getStopAddress(),
                        passenger.isAbsent(), passenger.isStopServed(), passenger.getArrivedAt()))
                .toList();
        return Trip.reconstitute(entity.getId(), entity.getRouteId(), entity.getDriverProfileId(),
                entity.getVehicleId(), entity.getServiceDate(), entity.getStatus(),
                entity.getShift(), entity.getCancelReason(), passengers);
    }

    public static TripPersistenceEntity toPersistenceFromDomain(Trip trip) {
        if (trip == null) return null;
        var entity = new TripPersistenceEntity();
        // Only set ID if the trip is being updated; for new trips JPA generates it
        if (trip.getId() != null) {
            entity.setId(trip.getId());
        }
        entity.setRouteId(trip.getRouteId());
        entity.setDriverProfileId(trip.getDriverProfileId());
        entity.setVehicleId(trip.getVehicleId());
        entity.setServiceDate(trip.getServiceDate());
        entity.setStatus(trip.getStatus());
        entity.setShift(trip.getShift());
        entity.setCancelReason(trip.getCancelReason());
        var passengers = new ArrayList<TripPassengerPersistenceEntity>();
        for (var passenger : trip.getPassengers()) {
            var persistencePassenger = new TripPassengerPersistenceEntity();
            if (passenger.getId() != null) {
                persistencePassenger.setId(passenger.getId());
            }
            persistencePassenger.setTrip(entity);
            persistencePassenger.setStudentId(passenger.getStudentId());
            persistencePassenger.setStopId(passenger.getStopId());
            persistencePassenger.setStopPosition(passenger.getStopPosition());
            persistencePassenger.setStopAddress(passenger.getStopAddress());
            persistencePassenger.setAbsent(passenger.isAbsent());
            persistencePassenger.setStopServed(passenger.isStopServed());
            persistencePassenger.setArrivedAt(passenger.getArrivedAt());
            passengers.add(persistencePassenger);
        }
        entity.setPassengers(passengers);
        return entity;
    }
}
