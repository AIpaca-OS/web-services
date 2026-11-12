# Profiles ↔ Routes: integration contract for AV2 (RMO-22, RMO-66)

## Branches and ownership

- Routes backend owner works only on `feature/routes`.
- Profiles backend owner works on `feature/profiles-and-relationship-management`.
- Routes frontend owner works on `feature/route-trip-planning`.
- Existing classes use different Java source roots: Routes (`com.aipaca.rumbo.routes`, Java 21) and Profiles (`com.aipaca.rumbo.platform.profiles`, Java 27). The team must reconcile the Maven parent, JDK target and Spring Boot application component scanning when integrating, without silently replacing either module.

The Routes backend already holds `Route`, `RouteStop`, `RouteSchedule` and `RouteAssignment`. Profiles remains the **owner** of private student data, consent/relationship status and driver's service permission. Do not copy student names, birthdates, guardian data or credentials into Routes tables.

## Needed Profiles backend contracts (NOT implemented by Routes)

These 3 endpoints must be implemented in the Profiles backend, protected using **actual driver authentication**. Route services forward `Authorization: Bearer <token>` to Profiles. Only Profiles verifies the token and resolves the driver identity from it.

### 1. GET /api/v1/profiles/driver/eligible-students

- Input: authenticated driver bearer token.
- Response 200: a minimal array of students eligible for **this driver**.
- Example: `[{"id":"student-1","fullName":"Example Student"}]`
- Return no birthdays, addresses, tutor data or unrelated students.
- 401: missing/invalid session, 403: not a driver; no anonymous broad GET /students.

### 2. GET /api/v1/profiles/driver/eligible-students/{studentId}?vehicleId={vehicleId}

- Input: authenticated driver, student ID and proposed vehicle ID.
- Response 200: `{"authorized":true}` **only if all checks pass**:
  - Student exists and `StudentStatus.ACTIVE`.
  - Tutor-student relationship `AuthorizationStatus.AUTHORIZED` and `revokedAt == null`.
  - There is a current authorization/enrollment for this driver to transport this student in this service.
  - The vehicle belongs to or is assigned to this authenticated driver.
- Response 200 `{"authorized":false}`, 403 or 404 for ineligible cases; no allowance on lookup failures.
- 401 if token is absent, invalid or expired.

### 3. GET /api/v1/profiles/driver/vehicles/{vehicleId}/access

- Input: authenticated driver and vehicle ID.
- Response 200: `{"authorized":true}` only if the driver may manage routes for that vehicle.
- Response 200 `{"authorized":false}`, 403 or 404 otherwise.
- This is required even for replacing assignments with an empty array (to prevent unauthorized removal).

## Routes endpoints already prepared

- `GET /api/v1/routes/eligible-students` → proxies the restricted Profiles catalog.
- `POST /api/v1/routes/{id}/assignments` → validates driver and student before assigning.
- `PUT /api/v1/routes/{id}/assignments` → **atomic** replacement of `[{"studentId":"student-1","stopId":"<uuid>"}]`; checks vehicle access, duplicates and every student before saving.
- `PATCH /api/v1/routes/{id}/status` with `{"status":"PUBLISHED"}` → checks route completeness and revalidates every assigned student in Profiles before publication.

The Routes adapter is enabled only by these Spring properties:

```properties
PROFILES_AUTHORIZATION_ENABLED=true
PROFILES_AUTHORIZATION_BASE_URL=https://<trusted-profiles-service-host>
```

Local: `PROFILES_AUTHORIZATION_BASE_URL=http://localhost:8080` only if a corresponding Profiles endpoint is actually exposed. When disabled or unreachable, the API **fails closed** with HTTP 503. Do **not** use a demo flag that assumes student authorization.

## IAM and Angular prerequisite

The current Angular `login` view only selects a role and navigates; it does not obtain a JWT. The IAM owner needs to provide a signed bearer token from a real login/session and attach it to the Angular HTTP calls (an authenticated HTTP interceptor is a natural integration point). Without a valid bearer token, the backend intentionally returns 401; never bypass this with `X-Driver-ID`, hardcoded credentials, or MockAPI.

The Routes Angular form now sends a complete list of selected assignments through `PUT /assignments` after the route and generated stop IDs have been saved. If Profiles rejects an assignment, it shows the error instead of claiming a successful complete route.

## Remaining work before demo can be claimed complete

- [ ] Profiles backend builds and exposes all three endpoints with real persistence.
- [ ] IAM verifies a signed driver token and propagates it from Angular.
- [ ] Profiles authorizes ACTIVE students and approved current relationship/enrollment for the authenticated driver.
- [ ] Merge verified bounded contexts into a compatible Java build and deployment, preserving existing work.
- [ ] Postman test valid/invalid driver, active/inactive student, authorized/revoked relationship, duplicate assignment and stale authorization at publish time.
- [ ] Protect route reads and driver ownership filtering before real student data is made available in production.
- [ ] Confirm `mvnw test`, `npm run build` and Render/Swagger responses.

## AV2 evidence

Capture the actual Swagger requests/responses, Postman results, `rp_route_assignments` rows in pgAdmin 4, Jira issues RMO-22/RMO-66 and the final commits. Do not mark integration done until the real Profiles endpoint and JWT verification have passed.
