package com.aipaca.rumbo.routes.domain.model.commands;
    import com.aipaca.rumbo.routes.domain.model.*;
import java.util.*;
import java.time.*;
    /** Domain intent. Validation and business rules live in the aggregate. */
    public record PublishRouteCommand(UUID routeId) {}
