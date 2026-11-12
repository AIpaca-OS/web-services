package com.aipaca.rumbo.routes.domain.model;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.time.LocalDate;
import java.util.*;
@Entity
@Table(name="rp_trips",uniqueConstraints=@UniqueConstraint(columnNames={"route_id","service_date"}))
public class Trip {
    @Id private UUID id;
    @Version private Long version;
    @Column(name="route_id",nullable=false) private UUID routeId;
    @Column(name="service_date",nullable=false) private LocalDate serviceDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TripStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private RouteShift shift;
    @Column(name="cancel_reason",length=500) private String cancelReason;
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name="rp_trip_passengers",joinColumns=@JoinColumn(name="trip_id"))
    private List<TripPassenger> passengers=new ArrayList<>();
    protected Trip(){}
    public Trip(Route route,LocalDate date){
        if(route.getStatus()!=RouteStatus.PUBLISHED)throw new IllegalArgumentException("Route must be published");
        if(!route.operatesOn(date.getDayOfWeek()))throw new IllegalArgumentException("Route does not operate on that day");
        id=UUID.randomUUID();routeId=route.getId();serviceDate=date;shift=route.getShift();status=TripStatus.SCHEDULED;
        for(var assignment:route.getAssignments()){
            var stop=route.getStops().stream().filter(s->s.getId().equals(assignment.getStopId())).findFirst().orElseThrow();
            passengers.add(new TripPassenger(assignment.getStudentId(),stop.getId(),stop.getPosition(),stop.getAddress()));
        }
        passengers.sort(Comparator.comparingInt(TripPassenger::getStopPosition));
    }
    public void markAbsent(String studentId){
        if(status!=TripStatus.SCHEDULED && status!=TripStatus.IN_PROGRESS)throw new IllegalArgumentException("Trip is no longer accepting absences");
        var passenger=passengers.stream().filter(p->p.getStudentId().equals(studentId)).findFirst()
            .orElseThrow(()->new IllegalArgumentException("Student is not on trip"));
        passenger.markAbsent();
    }
    public void start(){if(status!=TripStatus.SCHEDULED)throw new IllegalArgumentException("Trip cannot be started");status=TripStatus.IN_PROGRESS;}
    public void serveStop(UUID stopId){
        if(status!=TripStatus.IN_PROGRESS)throw new IllegalArgumentException("Trip is not in progress");
        var list=passengers.stream().filter(p->p.getStopId().equals(stopId)).toList();
        if(list.isEmpty())throw new IllegalArgumentException("Stop is not on trip");
        list.forEach(TripPassenger::markStopServed);
    }
    /** The driver's explicit confirmation, distinct from a served stop or completed journey. */
    public void confirmStudentArrival(String studentId) {
        if(status != TripStatus.IN_PROGRESS)
            throw new IllegalArgumentException("Arrival can only be confirmed during an active trip");
        var passenger = passengers.stream().filter(p -> p.getStudentId().equals(studentId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Student is not on trip"));
        passenger.confirmArrival();
    }
    public void cancel(String reason){
        if(status==TripStatus.CANCELLED || status==TripStatus.COMPLETED)throw new IllegalArgumentException("Trip cannot be cancelled");
        if(reason==null || reason.isBlank())throw new IllegalArgumentException("Cancellation reason is required");
        cancelReason=reason.trim();status=TripStatus.CANCELLED;
    }
    public void complete(){if(status!=TripStatus.IN_PROGRESS)throw new IllegalArgumentException("Trip is not in progress");status=TripStatus.COMPLETED;}
    public UUID getId(){return id;}public UUID getRouteId(){return routeId;}public LocalDate getServiceDate(){return serviceDate;}
    public TripStatus getStatus(){return status;}public RouteShift getShift(){return shift;}
    public String getCancelReason(){return cancelReason;}public List<TripPassenger> getPassengers(){return List.copyOf(passengers);}
}
