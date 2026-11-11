# Cambios de la reestructuración a Learning Center Platform

## Qué cambió

- Todos los bounded contexts y la clase principal están en `com.aipaca.rumbo.platform`.
- `routes` se separó en dos bounded contexts: `routes` (Route & Trip Planning: rutas, paradas, horario,
  asignaciones y publicación) y `trips` (Trip Execution & Monitoring: viajes y su ejecución). `trips` referencia la
  ruta solo por `RouteId` y guarda su propia copia de `RouteShift`, `StudentId` y los datos de cada parada.
- `routes`, `trips`, `vehicles` y `notifications` siguen el recorrido base de Learning Center:
  agregados sin JPA, value objects `record` con validación, entidades JPA que extienden
  `AuditableAbstractPersistenceEntity`, assemblers, adaptadores, command services con `Result` y controladores
  con OpenAPI.
- `shared` es una copia del `shared` de Learning Center Platform. Solo cambian el paquete y los metadatos de
  `OpenApiConfiguration` (nombre del proyecto, contacto, servidor local y sin el esquema `bearerAuth`, porque no hay
  IAM). La configuración CORS se centraliza en `shared`.
- `profiles` es la versión sin seguridad (parche `rumbo_profiles_learning_center_sin_seguridad`), incluido su
  `ProfilesRestExceptionHandler`.
- Todo `profiles` sigue la forma de codificar de Learning Center: comandos y queries con javadoc y validación con llaves,
  agregados con javadoc por campo y método, la entidad hija `TutorStudentRelationship` en `domain/model/entities`,
  servicios con campos `xxxRepository`, `// inherited javadoc` y `try/catch` hacia `Result`, controladores con OpenAPI
  y resources con `@Schema`. El estado de `ServiceEnrollment` pasó de `String` con constantes al enum
  `ServiceEnrollmentStatus` (mismos valores en base de datos y en JSON). Se eliminaron los cuatro eventos de dominio
  de `profiles`, que no se registraban ni publicaban (los eventos llegan en la segunda pasada).
- La vista previa en `prod` conserva el filtro `X-Rumbo-Preview-Key`. El perfil `dev` puede sembrar perfiles ficticios con `APP_PROFILES_DEMO_ENABLED=true`. La integración de IAM y el ACL de Profiles quedan pendientes.
- Recursos: solo `application.properties`, `application-dev.properties` y `application-prod.properties`. Variables
  de base de datos al estilo Learning Center: `DATABASE_URL` (host), `DATABASE_PORT`, `DATABASE_NAME`,
  `DATABASE_USER`, `DATABASE_PASSWORD`.
- Nuevo bounded context `incidents` (Incident & Delay Management), con el recorrido base de Learning Center:
  agregado `Incident` con la entidad `IncidentAcknowledgement` (confirmación de conocimiento de un tutor, una por
  tutor) y agregado `Delay`, cuyas actualizaciones crean un nuevo retraso que apunta al anterior
  (`previous_delay_id`, autorreferencia) y dejan el anterior como `SUPERSEDED`. Referencias: `TripId`, `StudentId`
  y `ProfileId`. Los retrasos e incidencias dejaron de ser notificaciones: se eliminó la categoría `ROUTE_NOTICE`.
- Referencias tipadas que pedía el informe y faltaban: `DriverProfileId` en `vehicles`, `routes`, `trips` y
  `billing` (antes `Long driverId`, misma columna `driver_id`); `SchoolId` en las paradas de `routes` y en los
  pasajeros de `trips`; `VehicleId` en `trips`; `ProfileId` del destinatario en `notifications`.
- Tablas en snake_case plural creadas por JPA: `routes`, `route_stops`, `route_assignments`, `route_schedules`,
  `trips` y `trip_passengers` (contexto `trips`), `vehicles`, `notifications`, `incidents`,
  `incident_acknowledgements` y `delays`. Los scripts
  `database/*-schema.sql` con las tablas `rp_*` y `vc_*` se eliminaron.

## Cambios de API para el frontend

| Antes | Ahora |
|---|---|
| IDs de rutas, paradas, asignaciones y viajes como UUID | IDs numéricos (`Long`) |
| `vehicleId` y `studentId` como texto | Números (`Long`), identidad de `vehicles` y de `profiles` |
| `DELETE` de rutas devuelve 204 | 200 con `{"message": ...}`, como en Learning Center |
| `POST /api/v1/routes/{id}/trips` con `{"date"}` | `POST /api/v1/trips` con el snapshot de la ruta: `{"routeId", "date", "shift", "passengers": [{"studentId", "stopId", "stopPosition", "stopAddress"}]}` (se arma con `GET /api/v1/routes/{id}`) |
| `GET /api/v1/routes/{id}/trips` | `GET /api/v1/trips?routeId={id}` |
| Comandos de `profiles` respondían `{"id": …}` | Responden el resource completo (`ProfileResource`, `StudentResource`, `ServiceEnrollmentResource`, `DataDeletionRequestResource`; autorizar tutor devuelve `TutorStudentRelationshipResource`). `id` sigue en el nivel superior |
| `GET` de `profiles` sin resultado respondía 404 vacío | 404 con `ProblemDetail` |
| — | Nuevo `GET /api/v1/service-enrollments/{enrollmentId}` |
| `DELETE /api/v1/routes/{id}` rechazaba rutas con viajes | Borra la ruta; los viajes conservan su `routeId` |
| `GET /api/v1/routes/eligible-students` | Eliminado (requiere ACL con `profiles`) |
| Cabecera `Authorization` en rutas | Ya no se usa |
| `POST /api/v1/routes/{routeId}/notices` | Retrasos e incidencias en `incidents`: `POST /api/v1/delays` con `{"tripId", "reportedByProfileId", "cause", "estimatedMinutes"}` y `POST /api/v1/incidents` con `{"tripId", "reportedByProfileId", "studentId", "category", "description"}`. `POST /api/v1/notifications/route-notices` se eliminó |
| — | Nuevos `GET /api/v1/incidents?tripId=`, `GET`/`PUT /api/v1/incidents/{id}`, `PATCH /api/v1/incidents/{id}/resolve`, `POST /api/v1/incidents/{id}/acknowledgements`, `GET /api/v1/delays?tripId=`, `GET /api/v1/delays/{id}` y `POST /api/v1/delays/{id}/updates` |
| `POST /api/v1/vehicles` sin dueño | `driverProfileId` obligatorio; `GET /api/v1/vehicles?driverProfileId=` |
| `POST /api/v1/routes` sin conductor | `driverProfileId` obligatorio; `schoolId` opcional en cada parada; `GET /api/v1/routes?driverProfileId=` |
| `POST /api/v1/trips` con `{"routeId", "date", "shift", "passengers"}` | Además `driverProfileId` y `vehicleId` obligatorios y `schoolId` opcional por pasajero; `GET /api/v1/trips` acepta `routeId` o `driverProfileId` |
| — | `recipientProfileId` opcional al crear notificaciones; `GET /api/v1/notifications?recipientProfileId=` |
| Tipos y prioridades de notificación en minúsculas | En mayúsculas (`INFO`, `REGULAR`); la entrada acepta cualquier caso |
| `routeId` de notificación siempre `0` | ID real de la ruta o `null` |
| `GET /api/v1/tutor/journeys`, `GET /api/v1/tutor/notifications` | El contexto `tutorjourneys` se integró en `notifications` como la categoría `STUDENT_JOURNEY`: `POST /api/v1/notifications/student-journeys`, `GET /api/v1/notifications?studentId={id}` y `PATCH /api/v1/notifications/{id}/read` |
| Notificaciones sin categoría | Campo `category` (`GENERAL`, `STUDENT_JOURNEY`), y `tripId` y `journeyEvent` en las de trayecto |
| `PATCH /api/v1/trips/{id}/passengers/{studentId}/arrived` solo en perfil `local` | Disponible siempre |
| Errores de rutas con `ProblemDetail` simple | `ProblemDetail` de `shared` con `code` y `details`; reglas de negocio 422, conflictos 409, no encontrado 404, validación 400. El JSON mal formado y un ID no numérico responden 400. Un endpoint inexistente responde 404 |

## Reglas que se retoman en la segunda pasada (eventos y ACL)

Estas reglas necesitaban leer otro contexto y se eliminaron para no cruzar contextos en el recorrido base:

- Elegibilidad del estudiante para el conductor al asignar y publicar rutas (`routes` → `profiles`).
- Al planificar un viaje: recibir el snapshot desde `routes` y validar que la ruta esté publicada y opere ese día; impedir borrar una ruta con viajes (`trips` ↔ `routes`).
- Existencia del vehículo de la ruta (`routes` → `vehicles`) y bloqueo del borrado de un vehículo en uso (`vehicles` → `routes`).
- Validar que el viaje de una incidencia o retraso exista y esté en curso (`incidents` → `trips`) y que el tutor que confirma una incidencia esté autorizado para el estudiante (`incidents` → `profiles`).
- Avisar a los tutores de incidencias, resoluciones y retrasos, sin incluir a los estudiantes ya entregados (`notifications` ← `incidents`, `trips`, `profiles`).
- Comprobar que cada `driverProfileId` sea un perfil de conductor (`vehicles`, `routes`, `trips`, `billing` → `profiles`).
- Notificación general al publicar una ruta y generación automática de avisos de trayecto desde los eventos de
  ruta y viaje (`notifications` ← `routes`, `trips`).

## Fuera de alcance por ahora

- Verificación de credenciales y documentos de conductores y vehículos (`vehicle_documents`, credenciales
  verificadas): el registro de vehículos sí está; su verificación llega con IAM.
- Otras tablas del informe que no están en esta versión: `trip_events`, `student_absences`,
  `notification_settings`, `recipient_authorizations`, `recipient_contacts` y `subscription_payments`.

## Migración de una base existente (por ejemplo en Render)

En `dev` Hibernate añade las columnas nuevas (`ddl-auto=update`), pero no puede agregar una columna `NOT NULL` a una
tabla con filas. En `prod` (`validate`) hay que crear el esquema antes de desplegar. En una base con datos:

```sql
ALTER TABLE vehicles ADD COLUMN driver_profile_id bigint;
UPDATE vehicles SET driver_profile_id = <id del conductor>;
ALTER TABLE vehicles ALTER COLUMN driver_profile_id SET NOT NULL;
-- Lo mismo para routes.driver_profile_id, trips.driver_profile_id y trips.vehicle_id.
ALTER TABLE route_stops ADD COLUMN school_id bigint;
ALTER TABLE trip_passengers ADD COLUMN school_id bigint;
ALTER TABLE notifications ADD COLUMN recipient_profile_id bigint;
DELETE FROM notifications WHERE category = 'ROUTE_NOTICE';
```

Las tablas `incidents`, `incident_acknowledgements` y `delays` se crean con `ddl-auto=update`, o con el DDL que
genera Hibernate para `prod`.
