package com.aipaca.rumbo.platform.routes.domain.repositories;
import com.aipaca.rumbo.platform.routes.domain.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface TripRepository extends JpaRepository<Trip,UUID> {
    boolean existsByRouteIdAndServiceDate(UUID routeId,LocalDate serviceDate);
    List<Trip> findByRouteIdOrderByServiceDateDesc(UUID routeId);
    boolean existsByRouteId(UUID routeId);
}
