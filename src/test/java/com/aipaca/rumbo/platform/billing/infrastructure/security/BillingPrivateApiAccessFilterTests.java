package com.aipaca.rumbo.platform.billing.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class BillingPrivateApiAccessFilterTests {

    private static MockHttpServletResponse call(
            BillingPrivateApiAccessFilter filter, String method,
            String path, String key, AtomicBoolean continued) throws Exception {
        var request = new MockHttpServletRequest(method, path);
        if (key != null) request.addHeader("X-Rumbo-Preview-Key", key);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) -> continued.set(true));
        return response;
    }

    @Test
    void publicPlansRemainReadableWithoutKey() throws Exception {
        var continued = new AtomicBoolean();
        var response = call(new BillingPrivateApiAccessFilter("private-test-secret"),
                "GET", "/api/v1/plans", null, continued);
        assertTrue(continued.get());
        assertEquals(200, response.getStatus());
    }

    @Test
    void allPrivateSubscriptionOperationsNeedKey() throws Exception {
        var guard = new BillingPrivateApiAccessFilter("private-test-secret");
        for (var method : new String[]{"GET", "POST", "PATCH"}) {
            var continued = new AtomicBoolean();
            var response = call(guard, method, "/api/v1/subscriptions",
                    null, continued);
            assertFalse(continued.get());
            assertEquals(401, response.getStatus());
        }
    }

    @Test
    void validKeyAllowsPrivateSubscriptionRequest() throws Exception {
        var continued = new AtomicBoolean();
        var response = call(new BillingPrivateApiAccessFilter("private-test-secret"),
                "PATCH", "/api/v1/subscriptions/1/cancel",
                "private-test-secret", continued);
        assertTrue(continued.get());
        assertEquals(200, response.getStatus());
    }

    @Test
    void missingSecretFailsClosed() throws Exception {
        var continued = new AtomicBoolean();
        var response = call(new BillingPrivateApiAccessFilter(""),
                "POST", "/api/v1/subscriptions", null, continued);
        assertFalse(continued.get());
        assertEquals(503, response.getStatus());
    }

    @Test
    void demoSessionsAndOtherBoundedContextsAreUnaffected() throws Exception {
        var guard = new BillingPrivateApiAccessFilter("private-test-secret");
        for (var path : new String[]{
                "/api/v1/billing-demo/subscriptions",
                "/api/v1/profiles",
                "/api/v1/routes"
        }) {
            var continued = new AtomicBoolean();
            var response = call(guard, "GET", path, null, continued);
            assertTrue(continued.get());
            assertEquals(200, response.getStatus());
        }
    }

    @Test
    void creatingNewPlansIsPrivateButOptionsRemainAvailable() throws Exception {
        var guard = new BillingPrivateApiAccessFilter("private-test-secret");
        var continued = new AtomicBoolean();
        assertEquals(401, call(guard, "POST", "/api/v1/plans",
                null, continued).getStatus());
        assertFalse(continued.get());
        continued = new AtomicBoolean();
        assertEquals(200, call(guard, "OPTIONS", "/api/v1/subscriptions",
                null, continued).getStatus());
        assertTrue(continued.get());
    }
}
