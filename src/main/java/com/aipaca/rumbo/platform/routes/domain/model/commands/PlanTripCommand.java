package com.aipaca.rumbo.platform.routes.domain.model.commands;
    import java.util.*;
import java.time.*;
    /** Domain intent. Validation and business rules live in the aggregate. */
    public record PlanTripCommand(UUID routeId, LocalDate date) {}
