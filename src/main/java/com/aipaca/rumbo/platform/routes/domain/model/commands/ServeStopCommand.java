package com.aipaca.rumbo.platform.routes.domain.model.commands;
    import java.util.*;

/** Domain intent. Validation and business rules live in the aggregate. */
    public record ServeStopCommand(UUID tripId, UUID stopId) {}
