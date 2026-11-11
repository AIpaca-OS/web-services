package com.aipaca.rumbo.routes.application;
import com.aipaca.rumbo.routes.application.ports.StudentAuthorizationPort;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.repositories.*;
import com.aipaca.rumbo.routes.interfaces.rest.RouteDtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;
@Service
@Transactional
public class RouteService {
    private final RouteRepository routes;
    private final TripRepository trips;
    private final StudentAuthorizationPort studentAuth;
    public RouteService(RouteRepository routes,TripRepository trips,StudentAuthorizationPort studentAuth){
        this.routes=routes;this.trips=trips;this.studentAuth=studentAuth;
    }
    @Transactional(readOnly=true) public List<Route> all(){return routes.findAll();}
    @Transactional(readOnly=true) public Route get(UUID id){return routes.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Route not found"));}
    public Route create(RouteInput input){
        var r=new Route(input.name(),input.shift(),input.vehicleId());
        apply(r,input);
        return routes.save(r);
    }
    public Route update(UUID id,RouteInput input){var r=get(id);r.rename(input.name());r.setShift(input.shift());
        r.setVehicleId(input.vehicleId());apply(r,input);return routes.save(r);
    }
    private void apply(Route r,RouteInput input){
        if(input.stops()!=null){var stops=new ArrayList<RouteStop>();int i=0;
            for(var s:input.stops())stops.add(new RouteStop(s.id(),s.address(),++i));r.replaceStops(stops);
        }
        if(input.schedule()!=null)r.updateSchedule(convert(input.schedule()),Boolean.TRUE.equals(input.scheduleConfirmed()));
    }
    private List<RouteSchedule> convert(List<ScheduleInput> inputs){
        var result=new ArrayList<RouteSchedule>();
        for(var s:inputs){try{result.add(new RouteSchedule(s.dayOfWeek(),LocalTime.parse(s.departureTime())));}
        catch(RuntimeException ex){throw new IllegalArgumentException("Invalid schedule time, expected HH:mm");}}
        return result;
    }
    public Route schedule(UUID id,ScheduleUpdate input){var r=get(id);r.updateSchedule(convert(input.schedule()),input.confirm());return routes.save(r);}
    @Transactional(readOnly=true) public List<RouteAssignment> assignments(UUID id){return get(id).getAssignments();}
    public RouteAssignment assign(UUID id,AssignmentInput input){var r=get(id);
        if(!studentAuth.isAuthorized(input.studentId(), r.getVehicleId(), null))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Student is not authorized");
        var a=r.assign(input.stopId(),input.studentId());routes.save(r);return a;
    }
    public Route publish(UUID id){var r=get(id);r.publish();return routes.save(r);}
    public void delete(UUID id){var r=get(id);
        if(trips.existsByRouteId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Route already has trips");
        routes.delete(r);
    }
    public Trip planTrip(UUID routeId,LocalDate date){var r=get(routeId);
        if(trips.existsByRouteIdAndServiceDate(routeId,date))throw new ResponseStatusException(HttpStatus.CONFLICT,"Trip already scheduled");
        return trips.save(new Trip(r,date));
    }
    @Transactional(readOnly=true) public Trip trip(UUID id){return trips.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Trip not found"));}
    @Transactional(readOnly=true) public List<Trip> trips(UUID routeId){get(routeId);return trips.findByRouteIdOrderByServiceDateDesc(routeId);}
    public Trip absent(UUID tripId,String studentId){var t=trip(tripId);t.markAbsent(studentId);return trips.save(t);}
    public Trip start(UUID tripId){var t=trip(tripId);t.start();return trips.save(t);}
    public Trip stopServed(UUID tripId,UUID stopId){var t=trip(tripId);t.serveStop(stopId);return trips.save(t);}
    public Trip cancel(UUID tripId,String reason){var t=trip(tripId);t.cancel(reason);return trips.save(t);}
    public Trip complete(UUID tripId){var t=trip(tripId);t.complete();return trips.save(t);}
}
