package com.aipaca.rumbo.tutorjourneys.interfaces.rest;

import com.aipaca.rumbo.platform.profiles.application.internal.access.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.routes.domain.model.*;
import com.aipaca.rumbo.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.routes.domain.repositories.TripRepository;
import com.aipaca.rumbo.tutorjourneys.domain.StudentJourneyNotice;
import com.aipaca.rumbo.tutorjourneys.domain.StudentJourneyNoticeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/tutor")
@org.springframework.context.annotation.Profile("local")
@ConditionalOnProperty(prefix = "app.profiles.demo", name = "enabled", havingValue = "true")
@Tag(name = "Tutor journeys", description = "Assigned routes, trip progress and student-specific notifications")
@Transactional(readOnly = true)
public class TutorJourneysController {
    public record StopView(int position, String address, boolean assignedToStudent) {}
    public record JourneyView(
            Long studentId, String studentName, UUID routeId, String routeName, String routeCode,
            String shift, String vehicleId, String departureTime,
            String assignedStopAddress, List<StopView> stops,
            UUID tripId, LocalDate serviceDate, String tripStatus,
            String studentStatus, boolean stopServed) {}
    public record NoticeView(
            Long id, Long studentId, UUID routeId, UUID tripId, String kind,
            String title, String message, java.time.Instant createdAt, boolean read) {}

    private final ProfilesAuthorization auth;
    private final StudentRepository students;
    private final RouteRepository routes;
    private final TripRepository trips;
    private final StudentJourneyNoticeRepository notices;

    public TutorJourneysController(ProfilesAuthorization auth, StudentRepository students,
                                  RouteRepository routes, TripRepository trips,
                                  StudentJourneyNoticeRepository notices) {
        this.auth = auth; this.students = students; this.routes = routes;
        this.trips = trips; this.notices = notices;
    }
    private List<Student> myStudents() {
        return students.findAllByAuthorizedTutorProfileId(auth.currentTutor().getId());
    }
    private List<String> myIds() {
        return myStudents().stream().map(s -> String.valueOf(s.getId())).toList();
    }

    @GetMapping("/journeys")
    @Operation(summary = "Published routes and the recorded trip state for my students")
    public List<JourneyView> journeys() {
        List<Student> mine = myStudents();
        if (mine.isEmpty()) return List.of();
        Map<String,Student> byId = new HashMap<>();
        mine.forEach(s -> byId.put(String.valueOf(s.getId()), s));
        List<JourneyView> response = new ArrayList<>();

        for (Route route : routes.findAll()) {
            if (route.getStatus() != RouteStatus.PUBLISHED) continue;
            List<RouteAssignment> assigned = route.getAssignments().stream()
                    .filter(a -> byId.containsKey(a.getStudentId())).toList();
            if (assigned.isEmpty()) continue;
            List<Trip> routeTrips = trips.findByRouteIdOrderByServiceDateDesc(route.getId());
            Trip latest = relevantTrip(routeTrips);
            for (RouteAssignment assignment : assigned) {
                Student student = byId.get(assignment.getStudentId());
                TripPassenger passenger = latest == null ? null : latest.getPassengers().stream()
                        .filter(p -> p.getStudentId().equals(assignment.getStudentId()))
                        .findFirst().orElse(null);
                var stop = route.getStops().stream()
                        .filter(s -> s.getId().equals(assignment.getStopId()))
                        .findFirst().orElse(null);
                var stops = route.getStops().stream()
                        .sorted(Comparator.comparingInt(RouteStop::getPosition))
                        .map(s -> new StopView(s.getPosition(), s.getAddress(),
                                s.getId().equals(assignment.getStopId()))).toList();
                response.add(new JourneyView(
                        student.getId(), student.getFullName(), route.getId(), route.getName(),
                        route.getCode(), route.getShift().name(), route.getVehicleId(),
                        route.getDepartureTime() == null ? "" : route.getDepartureTime().toString(),
                        stop == null ? "" : stop.getAddress(), stops,
                        latest == null ? null : latest.getId(),
                        latest == null ? null : latest.getServiceDate(),
                        latest == null ? "NOT_PLANNED" : latest.getStatus().name(),
                        studentState(latest, passenger), passenger != null && passenger.isStopServed()));
            }
        }
        response.sort(Comparator.comparing(JourneyView::studentName));
        return response;
    }
    private static Trip relevantTrip(List<Trip> trips) {
        if (trips.isEmpty()) return null;
        LocalDate today = LocalDate.now();
        return trips.stream().filter(t -> t.getStatus() == TripStatus.IN_PROGRESS)
                .findFirst().or(() -> trips.stream()
                        .filter(t -> t.getServiceDate().equals(today)).findFirst())
                .or(() -> trips.stream().filter(t -> t.getServiceDate().isAfter(today))
                        .min(Comparator.comparing(Trip::getServiceDate)))
                .orElse(trips.getFirst());
    }
    private static String studentState(Trip trip, TripPassenger passenger) {
        if (trip == null) return "ROUTE_PUBLISHED";
        if (passenger == null) return "NOT_ASSIGNED_ON_TRIP";
        if (passenger.isAbsent()) return "ABSENT";
        if (passenger.isArrived()) return "ARRIVED";
        if (trip.getStatus() == TripStatus.CANCELLED) return "CANCELLED";
        if (trip.getStatus() == TripStatus.COMPLETED) return "TRIP_COMPLETED";
        if (passenger.isStopServed()) return "STOP_SERVED";
        return trip.getStatus().name();
    }

    @GetMapping("/notifications")
    @Operation(summary = "Only lifecycle notices belonging to my linked students")
    public List<NoticeView> notifications() {
        List<String> ids = myIds();
        if (ids.isEmpty()) return List.of();
        return notices.findByStudentIdInOrderByCreatedAtDesc(ids).stream().limit(100)
                .map(n -> new NoticeView(n.getId(), Long.valueOf(n.getStudentId()), n.getRouteId(),
                        n.getTripId(), n.getKind(), n.getTitle(), n.getMessage(),
                        n.getCreatedAt(), n.isRead())).toList();
    }
    @PatchMapping("/notifications/{id}/read")
    @Transactional
    @Operation(summary = "Mark an authorized tutor notification as read")
    public NoticeView read(@PathVariable Long id) {
        StudentJourneyNotice notice = notices.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        Long studentId;
        try { studentId = Long.valueOf(notice.getStudentId()); }
        catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        auth.requireStudentAccess(studentId);
        notice.markAsRead();
        notices.save(notice);
        return new NoticeView(notice.getId(), studentId, notice.getRouteId(), notice.getTripId(),
                notice.getKind(), notice.getTitle(), notice.getMessage(),
                notice.getCreatedAt(), notice.isRead());
    }
}
