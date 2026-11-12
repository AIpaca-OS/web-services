package com.aipaca.rumbo.shared.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class DeploymentPreviewAccessFilterTests {
    private static final String SECRET = "test-only-secret";

    @Test void healthAndSwaggerStayPublic() throws Exception {
        var filter = new DeploymentPreviewAccessFilter(SECRET);
        for (String path : new String[]{"/actuator/health", "/swagger-ui/index.html", "/v3/api-docs"}) {
            var request = new MockHttpServletRequest("GET", path);
            var response = new MockHttpServletResponse();
            filter.doFilter(request, response, new MockFilterChain());
            assertEquals(200, response.getStatus(), path);
        }
    }

    @Test void publicPlansListDoesNotNeedPreviewKey() throws Exception {
        var filter = new DeploymentPreviewAccessFilter(SECRET);
        var response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/plans"),
                response, new MockFilterChain());
        assertEquals(200, response.getStatus());
    }

    @Test void privateRouteAndSubscriptionsRejectMissingKey() throws Exception {
        var filter = new DeploymentPreviewAccessFilter(SECRET);
        for (String path : new String[]{"/api/v1/routes", "/api/v1/trips",
                "/api/v1/subscriptions", "/api/v1/notifications", "/api/v1/vehicles"}) {
            var response = new MockHttpServletResponse();
            filter.doFilter(new MockHttpServletRequest("GET", path),
                    response, new MockFilterChain());
            assertEquals(401, response.getStatus(), path);
        }
    }

    @Test void validPreviewKeyAllowsPrivateRequests() throws Exception {
        var filter = new DeploymentPreviewAccessFilter(SECRET);
        var request = new MockHttpServletRequest("GET", "/api/v1/routes");
        request.addHeader("X-Rumbo-Preview-Key", SECRET);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals(200, response.getStatus());
    }

    @Test void missingDeploymentKeyFailsClosed() throws Exception {
        var filter = new DeploymentPreviewAccessFilter("");
        var response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/routes"),
                response, new MockFilterChain());
        assertEquals(503, response.getStatus());
    }
}
