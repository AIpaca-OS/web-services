package com.aipaca.rumbo.platform.routes.domain.model;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.DayOfWeek;
import java.time.LocalTime;
@Embeddable
public class RouteSchedule {
    @Enumerated(EnumType.STRING) @Column(name="day_of_week", nullable=false, length=9) private DayOfWeek dayOfWeek;
    @Column(name="departure_time", nullable=false) private LocalTime departureTime;
    protected RouteSchedule() {}
    public RouteSchedule(DayOfWeek dayOfWeek, LocalTime departureTime) {
        if (dayOfWeek == null || departureTime == null) throw new IllegalArgumentException("Day and time are required");
        this.dayOfWeek = dayOfWeek; this.departureTime = departureTime;
    }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getDepartureTime() { return departureTime; }
}
