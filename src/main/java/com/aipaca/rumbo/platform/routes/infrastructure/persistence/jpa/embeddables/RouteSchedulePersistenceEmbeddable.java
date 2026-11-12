package com.aipaca.rumbo.platform.routes.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Persistence representation for the {@code RouteSchedule} value object.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class RouteSchedulePersistenceEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 9)
    private DayOfWeek dayOfWeek;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    public RouteSchedulePersistenceEmbeddable(DayOfWeek dayOfWeek, LocalTime departureTime) {
        this.dayOfWeek = dayOfWeek;
        this.departureTime = departureTime;
    }
}
