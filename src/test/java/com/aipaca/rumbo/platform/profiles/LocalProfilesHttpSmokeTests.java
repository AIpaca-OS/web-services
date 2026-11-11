package com.aipaca.rumbo.platform.profiles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/** Real HTTP contract check against the loopback-only local demo, not a public IAM login. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.profiles.active=local",
                "app.profiles.demo.enabled=true",
                "spring.jpa.hibernate.ddl-auto=update",
                "server.port=0"
        })
class LocalProfilesHttpSmokeTests {
    @Value("${local.server.port}") int port;

    private HttpResponse<String> get(String path, String role) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(10)).GET();
        if (role != null) builder.header("X-Rumbo-Demo-Role", role);
        try (var client = HttpClient.newHttpClient()) {
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        }
    }

    @Test void schoolCatalogExposesAnActualSpringEndpoint() throws Exception {
        var response = get("/api/v1/schools",null);
        assertEquals(200,response.statusCode(),response.body());
        assertTrue(response.body().contains("Colegio Demostración"));
    }

    @Test void localTutorCanReadOwnProfileButMissingRoleIsRejected() throws Exception {
        var tutor = get("/api/v1/profiles/me","tutor");
        assertEquals(200,tutor.statusCode(),tutor.body());
        assertTrue(tutor.body().contains("TUTOR"));
        assertTrue(tutor.body().contains("10001"));

        var unauthorized = get("/api/v1/profiles/me",null);
        assertEquals(401,unauthorized.statusCode(),unauthorized.body());
    }

    @Test void driverRouteEligibleStudentsEndpointIsConnected() throws Exception {
        var response = get("/api/v1/routes/eligible-students","driver");
        assertEquals(200,response.statusCode(),response.body());
        assertTrue(response.body().startsWith("["));
    }

    @Test void seededDriverCatalogIsVisibleOnlyToLocalTutor() throws Exception {
        var tutor = get("/api/v1/local-demo/drivers", "tutor");
        assertEquals(200, tutor.statusCode(), tutor.body());
        assertTrue(tutor.body().contains("Conductor escolar"));

        var driver = get("/api/v1/local-demo/drivers", "driver");
        assertEquals(403, driver.statusCode(), driver.body());
    }
    private HttpResponse<String> post(String path, String role, String json) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("X-Rumbo-Demo-Role", role)
                .POST(HttpRequest.BodyPublishers.ofString(json));
        try (var client = HttpClient.newHttpClient()) {
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        }
    }

    private static long firstId(String json) {
        int start = json.indexOf("\"id\":");
        assertTrue(start >= 0, json);
        start += 5;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        return Long.parseLong(json.substring(start, end));
    }

    @Test void tutorRegistrationRequiresDriverAcceptanceBeforeRouteSelection() throws Exception {
        long schoolId = firstId(get("/api/v1/schools", null).body());
        long driverId = firstId(get("/api/v1/local-demo/drivers", "tutor").body());

        var registration = post("/api/v1/students", "tutor",
                "{\"firstName\":\"Estudiante\",\"lastName\":\"De Prueba\","
                + "\"birthDate\":\"2018-03-05\",\"schoolId\":" + schoolId
                + ",\"relationshipType\":\"OTHER\"}");
        assertEquals(201, registration.statusCode(), registration.body());
        long studentId = firstId(registration.body());

        var before = get("/api/v1/routes/eligible-students", "driver");
        assertEquals(200, before.statusCode(), before.body());
        assertFalse(before.body().contains("\"id\":\"" + studentId + "\""));

        var request = post("/api/v1/service-enrollments", "tutor",
                "{\"studentId\":" + studentId + ",\"driverProfileId\":" + driverId + "}");
        assertEquals(201, request.statusCode(), request.body());
        long requestId = firstId(request.body());

        var visibleForReview = get("/api/v1/service-enrollments/drivers/me/students", "driver");
        assertEquals(200, visibleForReview.statusCode(), visibleForReview.body());
        assertTrue(visibleForReview.body().contains("\"firstName\":\"Estudiante\""),
                visibleForReview.body());

        var stillPending = get("/api/v1/routes/eligible-students", "driver");
        assertFalse(stillPending.body().contains("\"id\":\"" + studentId + "\""));

        var accepted = post("/api/v1/service-enrollments/" + requestId + "/accept", "driver", "{}");
        assertEquals(200, accepted.statusCode(), accepted.body());
        var eligible = get("/api/v1/routes/eligible-students", "driver");
        assertEquals(200, eligible.statusCode(), eligible.body());
        assertTrue(eligible.body().contains("\"id\":\"" + studentId + "\""), eligible.body());
    }

}
