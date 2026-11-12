# Routes y Trips: migración controlada para AV2

Los controladores heredados de `com.aipaca.rumbo.routes` utilizan UUID. Los bounded contexts `com.aipaca.rumbo.platform.routes` y `com.aipaca.rumbo.platform.trips` siguen las capas de Learning Center y utilizan IDs numéricos.

Por defecto, `app.platform.routes-enabled=false` conserva la API y datos existentes, evitando controladores REST duplicados.

**Solo para una prueba local con datos ficticios y después de validar esquema PostgreSQL:** establecer `APP_PLATFORM_ROUTES_ENABLED=true` para habilitar los endpoints de Platform y deshabilitar los controladores REST heredados y sus avisos demo.

No se migra ni elimina ninguna tabla o ID en este PR. No activar en Render hasta validar la migración UUID/Long, las dependencias con Vehicles, Profiles, Tutor Journeys y Notifications, y pruebas de API y Angular en la misma versión.

Validar `./mvnw clean verify`, el estado de Swagger y la integración E2E antes de mover a `main`.
