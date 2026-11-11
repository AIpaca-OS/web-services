package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;
import com.aipaca.rumbo.platform.routes.domain.model.RouteSchedule;
import com.aipaca.rumbo.platform.routes.domain.model.RouteStop;
import com.aipaca.rumbo.platform.routes.domain.model.commands.CreateRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.commands.UpdateRouteCommand;
import com.aipaca.rumbo.platform.routes.domain.model.commands.UpdateRouteScheduleCommand;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteResources.*;
import java.time.LocalTime;
import java.util.*;
/** REST Resource -> Domain Command (UPC assembler pattern). */
public final class RouteCommandFromResourceAssembler {
    private RouteCommandFromResourceAssembler() {}
    public static CreateRouteCommand toCommand(RouteInput r) {
        return new CreateRouteCommand(r.name(),r.shift(),r.vehicleId(),stops(r.stops()),schedule(r.schedule()),Boolean.TRUE.equals(r.scheduleConfirmed()));
    }
    public static UpdateRouteCommand toCommand(UUID id, RouteInput r) {
        return toCommand(id, r, null);
    }
    public static UpdateRouteCommand toCommand(UUID id, RouteInput r, String authorizationHeader) {
        return new UpdateRouteCommand(id,r.name(),r.shift(),r.vehicleId(),stops(r.stops()),schedule(r.schedule()),Boolean.TRUE.equals(r.scheduleConfirmed()),authorizationHeader);
    }
    public static UpdateRouteScheduleCommand toCommand(UUID id, ScheduleUpdate resource) {
        return toCommand(id, resource, null);
    }
    public static UpdateRouteScheduleCommand toCommand(UUID id,ScheduleUpdate resource,String authorizationHeader) {
        return new UpdateRouteScheduleCommand(id,schedule(resource.schedule()),resource.confirm(),authorizationHeader);
    }
    private static List<RouteStop> stops(List<StopInput> resources) {
        if(resources==null)return List.of();
        var result=new ArrayList<RouteStop>();int n=0;
        for(var r:resources)result.add(new RouteStop(r.id(),r.address(),++n));
        return result;
    }
    private static List<RouteSchedule> schedule(List<ScheduleInput> resources) {
        if(resources==null)return List.of();
        var result=new ArrayList<RouteSchedule>();
        for(var r:resources){
            try { result.add(new RouteSchedule(r.dayOfWeek(),LocalTime.parse(r.departureTime()))); }
            catch(RuntimeException e) { throw new IllegalArgumentException("Invalid schedule time; expected HH:mm"); }
        }
        return result;
    }
}
