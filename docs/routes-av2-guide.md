# Routes — AV2 implementation and verification guide

## Scope and branches

- Jira project: RMO (RumboOS), Sprint 3.
- Backend: `web-services` → `feature/routes`.
- Frontend: `frontend-web-application` → `feature/route-trip-planning`.
- User stories: RMO-15, RMO-21, RMO-22, RMO-23 and RMO-28.
- Technical work: RMO-59, RMO-62, RMO-66 and RMO-68.

## Course architecture

The backend uses the same layered responsibilities emphasized by the course examples:

1. `domain/model`: Route, RouteStop, RouteSchedule, RouteAssignment and Trip.
2. `domain/model/commands` and `queries`: typed application intentions.
3. `application/commandservices` and `queryservices`: contracts.
4. `application/internal`: implementation of use cases.
5. `domain/repositories`: Spring Data JPA persistence contract.
6. `interfaces/rest/resources` and `transform`: request/response resources and assemblers.
7. `interfaces/rest`: controllers and OpenAPI documentation.

The Angular frontend uses `domain`, `application`, `infrastructure` and `presentation` layers.

## Local prerequisites

- JDK 21, IntelliJ IDEA and Maven wrapper.
- PostgreSQL database `rumbo`, administered with pgAdmin 4.
- Angular frontend, Node.js and npm.
- Spring Boot HTTP port 8080 and Angular port 4200.

Configure the environment locally (example names, not passwords):

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rumbo
SPRING_DATASOURCE_USERNAME=rumbo
SPRING_DATASOURCE_PASSWORD=<your-local-password>
CORS_ALLOWED_ORIGINS=http://localhost:4200
PORT=8080
```

Inspect `database/routes-schema.sql` and apply the schema to your local database **once**. The application uses `spring.jpa.hibernate.ddl-auto=none`, so tables are not created automatically.

Run backend with `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).

Run frontend with `npm install` followed by `npm start`. The local Angular proxy sends `/api` requests to `http://localhost:8080`.

When deploying to Render, configure PostgreSQL credentials and `CORS_ALLOWED_ORIGINS` using the frontend's exact deployed origin. Production Angular still needs a public API URL or a hosting reverse proxy for `/api/v1`; the local proxy is not a production reverse proxy.

## REST endpoints for Postman / Swagger

Swagger UI: `http://localhost:8080/swagger-ui/index.html` (verify exact path in your local setup).

| Method | Path | Purpose |
| --- | --- | --- |
| GET | /api/v1/routes | List routes |
| GET | /api/v1/routes/{id} | Get a route |
| POST | /api/v1/routes | Create route |
| PUT | /api/v1/routes/{id} | Replace route configuration |
| PUT | /api/v1/routes/{id}/schedule | Confirm or update the weekly schedule |
| GET | /api/v1/routes/{id}/assignments | List assignments (access control still pending) |
| GET | /api/v1/routes/eligible-students | List restricted candidates provided by Profiles |\n| POST | /api/v1/routes/{id}/assignments | Assign an authorized student |\n| PUT | /api/v1/routes/{id}/assignments | Atomically replace authorized student-to-stop assignments |
| PATCH | /api/v1/routes/{id}/status | Publish a valid route |
| DELETE | /api/v1/routes/{id} | Delete a route without trips |
| POST | /api/v1/routes/{id}/trips | Plan a trip |
| GET | /api/v1/routes/{id}/trips | List trips |

Sample request for `POST /api/v1/routes`:

```json
{
  "name": "Morning school route",
  "shift": "MORNING",
  "vehicleId": "vehicle-1",
  "stops": [
    {"address": "Av. Primavera 123"},
    {"address": "Av. Caminos del Inca 456"}
  ],
  "schedule": [
    {"dayOfWeek": "MONDAY", "departureTime": "07:00"},
    {"dayOfWeek": "TUESDAY", "departureTime": "07:00"}
  ],
  "scheduleConfirmed": true
}
```

Expected HTTP 201: backend-generated route UUID and code, ordered stops, schedule and `CONFIGURABLE` status. Check that the persisted rows appear in `rp_routes`, `rp_route_stops` and `rp_route_schedules`.

## Important limitations to disclose

- **Students integration:** Routes now includes a Profiles HTTP authorization adapter with explicit student eligibility, driver vehicle access and publication revalidation. It is disabled by default; `FailClosedStudentAuthorization` returns HTTP 503 until Profiles implements the verified endpoints and `PROFILES_AUTHORIZATION_ENABLED=true` is configured. Do not bypass this check using a hardcoded acceptance. See `docs/profiles-routes-integration-contract.md`.
- **Frontend assignments:** The Angular editor now saves route configuration and then sends its selected assignments with a separate atomic `PUT /assignments`. When the Profiles endpoint is unavailable or no real driver bearer token is provided, the request is rejected and the editor displays an error. The signed-token IAM login/interceptor is not yet implemented.
- **Driver access:** The protected assignment and publication endpoints delegate verification to Profiles, but other Routes read/update endpoints do not yet enforce authenticated driver filtering. They can include sensitive student references. Do not expose real student records publicly or describe the entire context as production-secure.
- **Production deployment:** Render publication and end-to-end frontend execution require verification; code commits are not proof of deployment.

## Review checklist

- [ ] Java tests: `./mvnw test` completed successfully.
- [ ] Angular tests: `npm test -- --watch=false` completed successfully.
- [ ] Angular build: `npm run build` completed successfully.
- [ ] Local PostgreSQL schema applied and route persisted.
- [ ] POST /routes returns generated code and stop order.
- [ ] PUT /schedule preserves schedule and confirms it.
- [ ] PATCH /status rejects incomplete routes.
- [ ] Assignment without Profiles integration responds 503 (fail closed).\n- [ ] Protected endpoints return 401 without a bearer token once Profiles integration is enabled.\n- [ ] Profiles returns active and authorized student candidates for the authenticated driver.\n- [ ] PUT /assignments rejects an unauthorized student without partially changing assignments.\n- [ ] PATCH /status revalidates authorizations and refuses revoked students.
- [ ] Swagger/OpenAPI and Postman responses captured.
- [ ] Render URL and deployed frontend URL tested.
- [ ] Jira statuses updated only after reviewing evidence.

Use screenshots and real terminal output for Development, Execution, Services Documentation and Software Deployment Evidence. Do not report tests or deployments as completed until verified.
