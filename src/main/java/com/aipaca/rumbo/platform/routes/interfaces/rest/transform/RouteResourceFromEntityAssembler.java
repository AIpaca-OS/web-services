package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;
import com.aipaca.rumbo.platform.routes.domain.model.Route;
import com.aipaca.rumbo.platform.routes.domain.model.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.model.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.Trip;

import java.util.*;
import java.util.stream.Collectors;
import static com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteResources.*;
public final class RouteResourceFromEntityAssembler {
    private RouteResourceFromEntityAssembler(){}
    public static RouteView view(Route r){
        var assignments=r.getAssignments().stream().collect(Collectors.toMap(RouteAssignment::getStopId, a->a,(a, b)->a));
        var stops=r.getStops().stream().sorted(Comparator.comparingInt(RouteStop::getPosition)).map(s->{
            var a=assignments.get(s.getId());
            return new StopView(s.getId(),s.getAddress(),s.getPosition(),a==null?null:a.getStudentId(),null);
        }).toList();
        var schedule=r.getSchedule().stream().map(s->new ScheduleView(s.getDayOfWeek(),s.getDepartureTime().toString())).toList();
        return new RouteView(r.getId(),r.getName(),r.getCode(),
                r.getDepartureTime()==null?"":r.getDepartureTime().toString(),r.getStatus(),r.getShift(),r.getVehicleId(),
                stops.size(),stops,schedule,r.isScheduleConfirmed());
    }
    public static AssignmentView view(RouteAssignment a){return new AssignmentView(a.getId(),a.getStopId(),a.getStudentId());}
    public static TripView view(Trip t){
        return new TripView(t.getId(),t.getRouteId(),t.getServiceDate(),t.getShift(),t.getStatus(),t.getCancelReason(),
            t.getPassengers().stream().map(p->new PassengerView(p.getStudentId(),p.getStopId(),p.getStopPosition(),p.getStopAddress(),p.isAbsent(),p.isStopServed(),p.isArrived())).toList());
    }
}
