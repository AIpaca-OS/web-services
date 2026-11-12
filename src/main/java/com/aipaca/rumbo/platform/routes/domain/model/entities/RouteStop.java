package com.aipaca.rumbo.platform.routes.domain.model.entities;

import lombok.Getter;

/** Ordered stop of a route, defined by a real address and its position. */
@Getter
public class RouteStop {
    private final Long id;
    private final String address;
    private final int position;

    public RouteStop(Long id, String address, int position) {
        if (id != null && id < 1)
            throw new IllegalArgumentException("Invalid stop id");
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("Stop address cannot be blank");
        if (position < 1)
            throw new IllegalArgumentException("Stop position must be positive");
        this.id = id;
        this.address = address.trim();
        this.position = position;
    }
}
