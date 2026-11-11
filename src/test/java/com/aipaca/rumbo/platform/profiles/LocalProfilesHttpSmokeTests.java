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
        assertTrue(tutor.body().contains("Conductor de demostración"));

        var driver = get("/api/v1/local-demo/drivers", "driver");
        assertEquals(403, driver.statusCode(), driver.body());
    }
}
