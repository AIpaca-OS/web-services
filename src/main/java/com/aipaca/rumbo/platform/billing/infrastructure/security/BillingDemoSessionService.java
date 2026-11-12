package com.aipaca.rumbo.platform.billing.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Ephemeral, pseudonymous bearer credentials for fictitious Billing demonstrations.
 * Not a substitute for driver login, JWT, or role-based authorization.
 * Every token is scoped to a randomly generated demo driver; real endpoints stay closed.
 */
@Service
public final class BillingDemoSessionService {
    private static final long SEVEN_DAYS_SECONDS = 7L * 24L * 60L * 60L;
    private static final long DEMO_DRIVER_ID_MIN = 1_000_000_000_000L;
    private final byte[] secret;
    private final SecureRandom random = new SecureRandom();

    private long currentWindow = -1L;
    private int issuedInWindow;

    public BillingDemoSessionService(
            @Value("${BILLING_DEMO_SIGNING_SECRET:}") String signingSecret) {
        this.secret = signingSecret.getBytes(StandardCharsets.UTF_8);
    }

    public synchronized String issueToken() {
        checkConfiguration();
        long minute = Instant.now().getEpochSecond() / 60L;
        if (minute != currentWindow) {
            currentWindow = minute;
            issuedInWindow = 0;
        }
        if (++issuedInWindow > 30) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Demo session limit reached. Try again shortly.");
        }
        long driverId = random.nextLong(DEMO_DRIVER_ID_MIN, Long.MAX_VALUE);
        long expires = Instant.now().getEpochSecond() + SEVEN_DAYS_SECONDS;
        String payload = "v1:" + driverId + ":" + expires + ":" + random.nextLong();
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encoded + "." + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(sign(encoded));
    }

    public long requireDriverId(String authorization) {
        checkConfiguration();
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw unauthorized();
        }
        String token = authorization.substring(7);
        String[] parts = token.split("\\.", -1);
        if (parts.length != 2 || token.length() > 512) throw unauthorized();
        try {
            byte[] actualSignature = Base64.getUrlDecoder().decode(parts[1]);
            if (!MessageDigest.isEqual(sign(parts[0]), actualSignature)) {
                throw unauthorized();
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[0]),
                    StandardCharsets.UTF_8);
            String[] fields = payload.split(":", 4);
            if (fields.length != 4 || !"v1".equals(fields[0])) throw unauthorized();
            long driverId = Long.parseLong(fields[1]);
            long expires = Long.parseLong(fields[2]);
            long now = Instant.now().getEpochSecond();
            if (driverId < DEMO_DRIVER_ID_MIN || expires <= now
                    || expires > now + SEVEN_DAYS_SECONDS) throw unauthorized();
            return driverId;
        } catch (IllegalArgumentException e) {
            throw unauthorized();
        }
    }

    private byte[] sign(String encoded) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(("rumbo-billing-demo:" + encoded)
                    .getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Billing demo signature unavailable", e);
        }
    }

    private void checkConfiguration() {
        if (secret.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Billing demo signing secret not configured");
        }
    }

    private static ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Demo session missing, invalid or expired");
    }
}
