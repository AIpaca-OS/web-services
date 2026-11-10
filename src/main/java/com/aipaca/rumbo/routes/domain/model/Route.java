package com.aipaca.rumbo.routes.domain.model;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
@Entity
@Table(name="rp_routes")
public class Route {
    @Id @Column(nullable=false) private UUID id;
    @Version private Long version;
    @Column(nullable=false, length=160) private String name;
    @Column(nullable=false, unique=true, length=40) private String code;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private RouteShift shift;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private RouteStatus status;
    @Column(name="vehicle_id", length=100) private String vehicleId;
    @Column(name="departure_time") private LocalTime departureTime;
    @Column(name="schedule_confirmed",nullable=false) private boolean scheduleConfirmed;
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name="rp_route_stops", joinColumns=@JoinColumn(name="route_id"))
    private List<RouteStop> stops = new ArrayList<>();
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name="rp_route_schedules", joinColumns=@JoinColumn(name="route_id"))
    private List<RouteSchedule> schedule = new ArrayList<>();
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name="rp_route_assignments", joinColumns=@JoinColumn(name="route_id"))
    private List<RouteAssignment> assignments = new ArrayList<>();
    protected Route() {}
    public Route(String name, RouteShift shift, String vehicleId) {
        id = UUID.randomUUID(); code = "R-" + id.toString().substring(0,8).toUpperCase(Locale.ROOT);
        status = RouteStatus.CONFIGURABLE; rename(name); this.shift = Objects.requireNonNull(shift);
        this.vehicleId=vehicleId;
    }
    public void rename(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Route name is required");
        this.name = name.trim();
    }
    public void setShift(RouteShift shift) { this.shift = Objects.requireNonNull(shift); }
    public void setVehicleId(String vehicleId) { this.vehicleId=vehicleId; }
    public void replaceStops(List<RouteStop> proposed) {
        if (proposed == null) throw new IllegalArgumentException("Stops cannot be null");
        var ids=new HashSet<UUID>();
        for (var stop: proposed) if (!ids.add(stop.getId())) throw new IllegalArgumentException("Duplicate stop id");
        var retained=new HashSet<UUID>(ids);
        if (assignments.stream().anyMatch(a->!retained.contains(a.getStopId())))
            throw new IllegalArgumentException("Cannot remove a stop with assigned students");
        stops.clear();
        for (int i=0; i<proposed.size(); i++) stops.add(new RouteStop(proposed.get(i).getId(), proposed.get(i).getAddress(), i+1));
    }
    public void updateSchedule(List<RouteSchedule> proposed, boolean confirmed) {
        if (proposed == null) throw new IllegalArgumentException("Schedule cannot be null");
        var days=new HashSet<DayOfWeek>();
        for (var row:proposed) if (!days.add(row.getDayOfWeek())) throw new IllegalArgumentException("Duplicate schedule day");
        if (confirmed && proposed.isEmpty()) throw new IllegalArgumentException("Confirmed schedule cannot be empty");
        schedule.clear(); schedule.addAll(proposed); scheduleConfirmed=confirmed;
        departureTime=schedule.isEmpty()?null:schedule.getFirst().getDepartureTime();
    }
    public RouteAssignment assign(UUID stopId, String studentId) {
        if (stops.stream().noneMatch(s->s.getId().equals(stopId))) throw new IllegalArgumentException("Stop is not part of route");
        if (assignments.stream().anyMatch(a->a.getStudentId().equals(studentId)))
            throw new IllegalArgumentException("Student is already assigned to route");
        var a=new RouteAssignment(stopId,studentId);assignments.add(a);return a;
    }
    public void validateForPublication() {
        if(stops.isEmpty() || !scheduleConfirmed || schedule.isEmpty() || assignments.isEmpty())
            throw new IllegalArgumentException("Publishing requires stops, confirmed schedule and an assigned student");
        if(vehicleId==null || vehicleId.isBlank())
            throw new IllegalArgumentException("Publishing requires a registered vehicle");
    }
    public void publish() {
        validateForPublication();
        status=RouteStatus.PUBLISHED;
    }
    public boolean operatesOn(DayOfWeek day) {return scheduleConfirmed && schedule.stream().anyMatch(s->s.getDayOfWeek()==day);}
    public UUID getId(){return id;} public Long getVersion(){return version;}
    public String getName(){return name;} public String getCode(){return code;} public RouteShift getShift(){return shift;}
    public RouteStatus getStatus(){return status;} public String getVehicleId(){return vehicleId;}
    public LocalTime getDepartureTime(){return departureTime;} public boolean isScheduleConfirmed(){return scheduleConfirmed;}
    public List<RouteStop> getStops(){return List.copyOf(stops);} public List<RouteSchedule> getSchedule(){return List.copyOf(schedule);}
    public List<RouteAssignment> getAssignments(){return List.copyOf(assignments);}
}
