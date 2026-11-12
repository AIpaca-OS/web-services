package com.aipaca.rumbo.platform.routes.domain.model.entities;

import com.aipaca.rumbo.platform.routes.domain.model.valueobjects.SchoolId;
import lombok.Getter;

/**
 * Route stop entity.
 *
 * <p>An ordered stop with identity inside the {@code Route} aggregate.
 * The identifier stays null until the stop is persisted. A stop may serve a school, a school of
 * the Profiles bounded context kept only through its {@link SchoolId}.</p>
 */
@Getter
public class RouteStop {
    private Long id;
    private String address;
    private SchoolId schoolId;
    private int position;

    /**
     * Creates a stop that serves no school.
     *
     * @param id       The stop id, or null for a new stop.
     * @param address  The stop address. It cannot be null or blank.
     * @param position The stop position inside the route, starting at 1.
     */
    public RouteStop(Long id, String address, int position) {
        this(id, address, null, position);
    }

    /**
     * Creates a stop with the given position.
     *
     * @param id       The stop id, or null for a new stop.
     * @param address  The stop address. It cannot be null or blank.
     * @param schoolId The school served at the stop, or null.
     * @param position The stop position inside the route, starting at 1.
     */
    public RouteStop(Long id, String address, SchoolId schoolId, int position) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Stop address cannot be null or blank");
        }
        if (position < 1) {
            throw new IllegalArgumentException("Stop position must be greater than 0");
        }
        this.id = id;
        this.address = address.trim();
        this.schoolId = schoolId;
        this.position = position;
    }

    public Long getSchoolIdValue() {
        return schoolId == null ? null : schoolId.schoolId();
    }
}
