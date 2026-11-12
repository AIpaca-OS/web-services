package com.aipaca.rumbo.platform.billing.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class BillingDemoSessionServiceTests {
    private final BillingDemoSessionService service =
            new BillingDemoSessionService("this-is-a-fictitious-test-only-secret-over-32-bytes");

    @Test
    void issuedTokenIdentifiesOnePseudonymousDriver() {
        var token = service.issueToken();
        long driver = service.requireDriverId("Bearer " + token);
        assertTrue(driver >= 1_000_000_000_000L);
        assertEquals(driver, service.requireDriverId("Bearer " + token));
        assertNotEquals(driver,
                service.requireDriverId("Bearer " + service.issueToken()));
    }

    @Test
    void missingOrTamperedTokenCannotAccessAnotherSession() {
        var token = service.issueToken();
        assertThrows(ResponseStatusException.class,
                () -> service.requireDriverId(null));
        assertThrows(ResponseStatusException.class,
                () -> service.requireDriverId("Bearer " + token + "modified"));
    }

    @Test
    void startupWithMissingSecretKeepsDemoClosed() {
        var unconfigured = new BillingDemoSessionService("");
        assertThrows(ResponseStatusException.class, unconfigured::issueToken);
    }
}
