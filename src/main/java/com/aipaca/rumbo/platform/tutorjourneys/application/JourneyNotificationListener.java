package com.aipaca.rumbo.platform.tutorjourneys.application;

import com.aipaca.rumbo.platform.routes.domain.events.RoutePublishedEvent;
import com.aipaca.rumbo.platform.routes.domain.events.DriverRouteNoticeEvent;
import com.aipaca.rumbo.platform.routes.domain.events.TripProgressEvent;
import com.aipaca.rumbo.platform.routes.domain.model.RouteAssignment;
import com.aipaca.rumbo.platform.routes.domain.repositories.RouteRepository;
import com.aipaca.rumbo.platform.tutorjourneys.domain.StudentJourneyNotice;
import com.aipaca.rumbo.platform.tutorjourneys.domain.StudentJourneyNoticeRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Persists guardian-facing lifecycle messages in the same transaction as route/trip updates.
 * Student-bound notifications are only read through tutor access checks.
 */
@Component
@org.springframework.context.annotation.Profile("local")
@ConditionalOnProperty(prefix = "app.profiles.demo", name = "enabled", havingValue = "true")
public class JourneyNotificationListener {
    private final StudentJourneyNoticeRepository notices;
    private final RouteRepository routes;

    public JourneyNotificationListener(StudentJourneyNoticeRepository notices, RouteRepository routes) {
        this.notices = notices;
        this.routes = routes;
    }
    private void store(String studentId, UUID routeId, UUID tripId,
                       String kind, String title, String message) {
        String key = routeId + ":" + (tripId == null ? "publication" : tripId)
                + ":" + kind + ":" + studentId;
        if (!notices.existsByEventKey(key)) {
            notices.save(new StudentJourneyNotice(
                    studentId, routeId, tripId, key, kind, title, message));
        }
    }
    @EventListener
    public void onDriverNotice(DriverRouteNoticeEvent event) {
        routes.findById(event.routeId()).ifPresent(route -> {
            String title = "DELAY".equals(event.kind())
                    ? "Retraso de ruta" : "Incidencia de ruta";
            for (RouteAssignment assignment : route.getAssignments()) {
                String key = route.getId() + ":notice:" + event.eventId() + ":"
                        + assignment.getStudentId();
                if (!notices.existsByEventKey(key)) {
                    notices.save(new StudentJourneyNotice(
                            assignment.getStudentId(), route.getId(), null, key,
                            event.kind(), title, event.message()));
                }
            }
        });
    }
    @EventListener
    public void onPublished(RoutePublishedEvent event) {
        routes.findById(event.routeId()).ifPresent(route -> {
            for (RouteAssignment assignment : route.getAssignments()) {
                store(assignment.getStudentId(), route.getId(), null,
                        "ROUTE_PUBLISHED", "Ruta publicada",
                        "La ruta " + route.getName() + " ya está disponible para su consulta.");
            }
        });
    }
    @EventListener
    public void onTripEvent(TripProgressEvent event) {
        String title;
        String message;
        switch (event.kind()) {
            case "TRIP_PLANNED" -> {
                title = "Viaje programado"; message = "Se programó un viaje para la ruta del estudiante.";
            }
            case "TRIP_STARTED" -> {
                title = "Viaje iniciado"; message = "El conductor marcó el inicio del recorrido.";
            }
            case "STOP_SERVED" -> {
                title = "Parada atendida"; message = "El conductor marcó como atendida la parada asignada al estudiante.";
            }
            case "ABSENT" -> {
                title = "Ausencia registrada"; message = "El estudiante fue marcado como ausente en el viaje.";
            }
            case "TRIP_CANCELLED" -> {
                title = "Viaje cancelado"; message = "El conductor canceló el recorrido.";
            }
            case "STUDENT_ARRIVED" -> {
                title = "Llegada confirmada";
                message = "El conductor confirmó la llegada del estudiante al destino.";
            }
            case "TRIP_COMPLETED" -> {
                title = "Viaje completado"; message = "El conductor marcó el viaje como finalizado. No implica verificación independiente de llegada.";
            }
            default -> { return; }
        }
        for (String studentId : event.studentIds()) {
            store(studentId, event.routeId(), event.tripId(), event.kind(), title, message);
        }
    }
}
