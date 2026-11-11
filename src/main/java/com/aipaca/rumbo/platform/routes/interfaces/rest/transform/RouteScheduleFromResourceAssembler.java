package com.aipaca.rumbo.platform.routes.interfaces.rest.transform;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.RouteSchedule;
import com.aipaca.rumbo.platform.routes.interfaces.rest.resources.RouteScheduleResource;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Assembler to convert schedule resources to {@link RouteSchedule} value objects.
 */
public class RouteScheduleFromResourceAssembler {
    /**
     * Converts a list of RouteScheduleResource to RouteSchedule value objects.
     *
     * @param resources The {@link RouteScheduleResource} resources, may be null.
     * @return The {@link RouteSchedule} value objects.
     */
    public static List<RouteSchedule> toValueObjectsFromResources(List<RouteScheduleResource> resources) {
        if (resources == null) return List.of();
        return resources.stream()
                .map(resource -> new RouteSchedule(resource.dayOfWeek(), toTime(resource.departureTime())))
                .toList();
    }

    private static LocalTime toTime(String value) {
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid schedule time; expected HH:mm");
        }
    }
}
