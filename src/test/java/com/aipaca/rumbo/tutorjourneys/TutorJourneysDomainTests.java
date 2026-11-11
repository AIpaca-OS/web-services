package com.aipaca.rumbo.tutorjourneys;

import com.aipaca.rumbo.tutorjourneys.domain.StudentJourneyNotice;
import com.aipaca.rumbo.routes.domain.events.TripProgressEvent;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TutorJourneysDomainTests {
    @Test void noticeCanBeMarkedRead() {
        UUID routeId = UUID.randomUUID();
        StudentJourneyNotice notice = new StudentJourneyNotice(
                "12", routeId, null, routeId + ":publication:ROUTE_PUBLISHED:12",
                "ROUTE_PUBLISHED", "Ruta publicada", "Ruta disponible");
        assertFalse(notice.isRead());
        notice.markAsRead();
        assertTrue(notice.isRead());
    }
    @Test void tripEventCopiesPassengerIds() {
        var list = new java.util.ArrayList<String>(List.of("12"));
        TripProgressEvent event = new TripProgressEvent(
                UUID.randomUUID(), UUID.randomUUID(), "TRIP_STARTED", list);
        list.clear();
        assertEquals(List.of("12"), event.studentIds());
    }
}
