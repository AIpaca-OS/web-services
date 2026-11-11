"""Exercise the Angular REST contracts against the disposable PostgreSQL CI database."""
import datetime
import json
import os
from urllib.error import HTTPError
from urllib.request import Request, urlopen

API = "http://127.0.0.1:8080/api/v1"
KEY = os.environ["DEPLOYMENT_ACCESS_TOKEN"]


def call(method, path, payload=None, public=False):
    headers = {"Origin": "http://localhost:4200"}
    if not public:
        headers["X-Rumbo-Preview-Key"] = KEY
    data = None
    if payload is not None:
        headers["Content-Type"] = "application/json"
        data = json.dumps(payload).encode()
    try:
        with urlopen(Request(API + path, data=data, headers=headers, method=method), timeout=10) as response:
            assert response.headers["Access-Control-Allow-Origin"] == headers["Origin"]
            return json.load(response)
    except HTTPError as error:
        raise RuntimeError(f"{method} {path}: HTTP {error.code} {error.read().decode()}") from error


def profile(account_id, role):
    return call("POST", "/profiles", {
        "accountId": account_id, "profileType": role,
        "firstName": role.title(), "lastName": "Demo", "phoneNumber": "900000000",
    })["id"]


driver = profile(810001, "DRIVER")
tutor = profile(810002, "TUTOR")
assert call("GET", "/profiles/by-account/810001")["id"] == driver
student = call("POST", "/students", {
    "tutorProfileId": tutor, "firstName": "Estudiante", "lastName": "Demo",
    "birthDate": "2016-05-03", "schoolId": None, "relationshipType": "OTHER",
})["id"]
enrollment = call("POST", "/service-enrollments", {
    "studentId": student, "driverProfileId": driver, "requestedByProfileId": tutor,
})["id"]
accepted = call("POST", f"/service-enrollments/{enrollment}/accept?driverProfileId={driver}")
assert accepted["status"] == "ACCEPTED"
assert student in {row["id"] for row in call("GET", f"/service-enrollments/drivers/{driver}/students")}

vehicle = call("POST", "/vehicles", {
    "driverProfileId": driver, "brand": "Toyota", "model": "Hiace",
    "licensePlate": "CI-123", "capacity": 15, "year": 2023,
})["id"]
assert vehicle in {row["id"] for row in call("GET", f"/vehicles?driverProfileId={driver}")}

service_date = datetime.date.today() + datetime.timedelta(days=1)
route = call("POST", "/routes", {
    "driverProfileId": driver, "vehicleId": vehicle, "name": "Ruta CI", "shift": "MORNING",
    "stops": [{"address": "Av. Demo 123", "schoolId": None}],
    "schedule": [{"dayOfWeek": service_date.strftime("%A").upper(), "departureTime": "07:00"}],
    "scheduleConfirmed": True,
})
route_id, stop_id = route["id"], route["stops"][0]["id"]
route = call("PUT", f"/routes/{route_id}/assignments", [{"studentId": student, "stopId": stop_id}])
assert route["stops"][0]["studentIds"] == [student]
route = call("PATCH", f"/routes/{route_id}/status", {"status": "PUBLISHED"})
assert route["status"] == "PUBLISHED"
stop = route["stops"][0]
trip = call("POST", "/trips", {
    "routeId": route_id, "driverProfileId": driver, "vehicleId": vehicle,
    "date": service_date.isoformat(), "shift": route["shift"],
    "passengers": [{
        "studentId": student, "stopId": stop_id, "stopAddress": stop["address"],
        "stopPosition": stop["position"], "schoolId": stop["schoolId"],
    }],
})
trip_id = trip["id"]
assert trip_id in {row["id"] for row in call("GET", f"/trips?routeId={route_id}")}
call("PATCH", f"/trips/{trip_id}/start")
call("POST", "/incidents", {
    "tripId": trip_id, "reportedByProfileId": driver, "category": "OTHER",
    "description": "Prueba de integración",
})
call("POST", "/delays", {
    "tripId": trip_id, "reportedByProfileId": driver, "cause": "Tráfico de prueba",
    "estimatedMinutes": 10,
})

plan = call("POST", "/plans", {
    "name": "Plan CI", "description": "Catálogo ficticio de integración", "referencePrice": 29.90,
    "currency": "PEN", "billingPeriodMonths": 1, "vehicleLimit": 1, "active": True,
})["id"]
assert plan in {row["id"] for row in call("GET", "/plans", public=True)}
print("Profiles, Vehicles, Routes, Trips, Incidents and public Plans passed.")
