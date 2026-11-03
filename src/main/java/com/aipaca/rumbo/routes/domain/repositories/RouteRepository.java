package com.aipaca.rumbo.routes.domain.repositories;
import com.aipaca.rumbo.routes.domain.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface RouteRepository extends JpaRepository<Route,UUID> {}
