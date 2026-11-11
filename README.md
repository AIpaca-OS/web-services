# Rumbo — Web Services

Backend del producto Rumbo, desarrollado por AIpaca-OS para el curso Desarrollo de Aplicaciones Open Source — UPC.
Sigue la arquitectura de **Learning Center Platform**: Domain-Driven Design en cuatro capas (domain, infrastructure,
application, interfaces), CQRS y `Result<T, ApplicationError>`.

> La demostración local usa perfiles ficticios. En Render el catálogo de planes se puede leer públicamente y los demás endpoints requieren la clave privada de vista previa. El control de permisos por usuario queda pendiente de IAM.

## Tecnologías
- Java 27, Spring Boot 4, Spring Data JPA (Hibernate), Bean Validation
- PostgreSQL
- SpringDoc OpenAPI / Swagger UI
- Lombok y `pluralize` (estrategia de nombres de tablas)

## Estructura

Todos los bounded contexts están bajo `com.aipaca.rumbo.platform`:

| Paquete | Responsabilidad |
|---|---|
| `shared` | Copia del `shared` de Learning Center Platform (solo cambia el paquete y los metadatos de OpenAPI): raíz de dominio, entidad auditable, estrategia de nombres, `Result`/`ApplicationError`, `GlobalExceptionHandler`, OpenAPI, eventos, i18n e hilos virtuales |
| `profiles` | Perfiles, colegios, estudiantes, tutores, matrículas de servicio y solicitudes de eliminación de datos |
| `billing` | Planes y suscripciones de conductores |
| `vehicles` | Registro de vehículos de cada conductor (la verificación de documentos y credenciales aún no está) |
| `routes` | Route & Trip Planning: rutas, paradas, horario semanal, asignación de estudiantes y publicación |
| `trips` | Trip Execution & Monitoring: viajes planificados desde una ruta publicada y su ejecución (inicio, ausencias, paradas atendidas, llegadas, cancelación y cierre) |
| `incidents` | Incident & Delay Management: incidencias de un viaje (categoría, descripción, resolución y confirmaciones de conocimiento de los tutores) y retrasos con su historial de estimaciones |
| `notifications` | Notificaciones por categoría: `GENERAL` y `STUDENT_JOURNEY` (hechos del trayecto de un estudiante para sus tutores), con destinatario opcional |

Cada contexto contiene `domain/model/{aggregates,entities,valueobjects,commands,queries}`, `domain/repositories`,
`infrastructure/persistence/jpa/{converters,embeddables,entities,assemblers,repositories,adapters}`,
`application/{commandservices,queryservices,internal}` e `interfaces/rest/{resources,transform}`.

**Sin cruces entre contextos.** Un contexto solo importa `shared`. Cuando un contexto necesita referirse a algo de
otro, guarda solo su identidad mediante un value object local terminado en `Id` (por ejemplo `StudentId` o
`VehicleId` en `routes`) y lo persiste con un converter, sin clave foránea entre contextos. `PlatformArchitectureTests` hace fallar el build si
aparece un import entre contextos.

El filtro CORS de `shared` lee `CORS_ALLOWED_ORIGINS` para `/api/v1/**` (por defecto `http://localhost:4200`, separado por comas). Para GitHub Pages, usa el origen `https://aipaca-os.github.io` sin el path del repositorio.

El código adaptado de Learning Center conserva su [aviso MIT](docs/LEARNING_CENTER_LICENSE.md).

Referencias entre contextos:

| Contexto | Referencias (value object local → contexto dueño) |
|---|---|
| `billing` | `DriverProfileId` → `profiles` |
| `vehicles` | `DriverProfileId` (dueño) → `profiles` |
| `routes` | `DriverProfileId` (conductor), `SchoolId` (parada) → `profiles`; `StudentId` → `profiles`; `VehicleId` → `vehicles` |
| `trips` | `RouteId` → `routes`; `DriverProfileId`, `StudentId`, `SchoolId` (pasajero) → `profiles`; `VehicleId` → `vehicles` |
| `incidents` | `TripId` → `trips`; `ProfileId` (quien reporta y tutor que confirma), `StudentId` → `profiles` |
| `notifications` | `RouteId` → `routes`; `TripId` → `trips`; `StudentId`, `ProfileId` (destinatario) → `profiles` |

## Ejecutar

Los únicos recursos son `application.properties`, `application-dev.properties` y `application-prod.properties`.
El perfil por defecto es `dev`.

```bash
createdb rumbo
APP_PROFILES_DEMO_ENABLED=true ./mvnw spring-boot:run
```

Variables opcionales en `dev`: `DATABASE_URL` (host, por defecto `localhost`), `DATABASE_PORT` (5432),
`DATABASE_NAME` (`rumbo`), `DATABASE_USER` (`postgres`), `DATABASE_PASSWORD` (`postgres`), `PORT` (8080).
En `dev` el servidor escucha solo en `127.0.0.1`. Hibernate crea o actualiza el esquema (`ddl-auto=update`).

La semilla opcional registra un tutor y un conductor ficticios más un colegio en el perfil `dev`.

En `prod`, usa `RENDER_POSTGRES_URL` o las variables `DATABASE_*`. El esquema se valida por defecto. Para la primera prueba de integración, usa una base vacía de pruebas con `SPRING_JPA_DDL_AUTO=update`. Antes de reutilizar una base con datos, planifica una migración para las tablas y los IDs que cambiaron. Configura `DEPLOYMENT_ACCESS_TOKEN` para probar los endpoints privados desde Swagger o Postman. El servicio existente es [rumbo-team-backend](https://rumbo-team-backend.onrender.com), vinculado a `main`. Para conectar GitHub Pages al servicio existente, configura CORS desde su panel Environment.

Swagger UI: <http://localhost:8080/swagger-ui/index.html>. La lista completa de endpoints está en
`docs/ENDPOINTS_RUBRICA.csv`.

## Pruebas

```bash
./mvnw verify
```

La mayoría de pruebas no necesitan base de datos (dominio, assemblers, command services con mocks y mapeo JPA
validado sin conexión). `RumboWebServicesApplicationTests` levanta el contexto completo y requiere PostgreSQL.

## Pendiente: segunda pasada (eventos y ACL)

Esta versión implementa el recorrido base de cada contexto. Las reglas que necesitan consultar otro contexto se
añadirán con eventos de integración y fachadas ACL (`interfaces/acl`, `interfaces/events`), como en Learning Center:

- `routes`: verificar que el estudiante asignado sea elegible para el conductor (`profiles`) y que el vehículo exista (`vehicles`); impedir borrar una ruta que ya tiene viajes (`trips`).
- `trips`: recibir el snapshot de la ruta desde `routes` (hoy viaja en el cuerpo de `POST /api/v1/trips`) y validar que la ruta esté publicada y opere ese día.
- `vehicles`: impedir borrar un vehículo asignado a una ruta (`routes`).
- `incidents`: validar que el viaje exista y esté en curso (`trips`) y que quien confirma sea tutor autorizado del estudiante (`profiles`).
- `notifications`: generar las notificaciones `STUDENT_JOURNEY` a partir de los eventos de ruta y viaje, y avisar a los tutores de las incidencias y retrasos (`incidents`), excluyendo a los estudiantes ya entregados (`trips`) y dirigiéndolas solo a los tutores autorizados (`profiles`).
- `vehicles`, `routes`, `trips`, `billing`: comprobar que el `driverProfileId` sea un perfil de conductor (`profiles`).
