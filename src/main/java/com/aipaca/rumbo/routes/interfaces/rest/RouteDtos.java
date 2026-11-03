package com.aipaca.rumbo.routes.interfaces.rest;
import com.aipaca.rumbo.routes.domain.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
public final class RouteDtos {
    private RouteDtos(){}
    public record StopInput(UUID id,@NotBlank String address,String studentId) {}
    public record ScheduleInput(@NotNull DayOfWeek dayOfWeek,@NotBlank String departureTime) {}
    public record RouteInput(@NotBlank String name,@NotNull RouteShift shift,String vehicleId,String departureTime,
            List<@Valid StopInput> stops,List<@Valid ScheduleInput> schedule,Boolean scheduleConfirmed) {}
    public record ScheduleUpdate(@NotNull List<@Valid ScheduleInput> schedule, boolean confirm) {}
    public record AssignmentInput(@NotBlank String studentId,@NotNull UUID stopId) {}
    public record StatusUpdate(@NotNull RouteStatus status) {}
    public record StopView(UUID id,String address,int position,String studentId,String studentName) {}
    public record ScheduleView(DayOfWeek dayOfWeek,String departureTime) {}
    public record AssignmentView(UUID id,UUID stopId,String studentId) {}
    public record RouteView(UUID id,String name,String code,String departureTime,RouteStatus status,RouteShift shift,
          String vehicleId,int stopsCount,List<StopView> stops,List<ScheduleView> schedule,boolean scheduleConfirmed) {}
    public record TripInput(@NotNull LocalDate date) {}
    public record AbsenceInput(@NotBlank String studentId) {}
    public record CancelInput(@NotBlank String reason) {}
    public record PassengerView(String studentId,UUID stopId,int stopPosition,String stopAddress,boolean absent,boolean stopServed) {}
    public record TripView(UUID id,UUID routeId,LocalDate serviceDate,RouteShift shift,TripStatus status,
      String cancelReason,List<PassengerView> students) {}
}
