package com.aipaca.rumbo.platform.profiles.application.internal.access;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Deliberately non-authenticating, loopback-only identity for fictional Sprint 3 demo data.
 * Never enable on a public server or with real personal/student records.
 * Default ActorIdentity keeps all private resources fail-closed until IAM is delivered.
 */
@Component
@Primary
@Profile("local")
@ConditionalOnProperty(prefix="app.profiles.demo",name="enabled",havingValue="true")
public final class LocalDemoActorIdentity implements ActorIdentity {
    public static final long TUTOR_ACCOUNT_ID = 10001L;
    public static final long DRIVER_ACCOUNT_ID = 10002L;
    private static HttpServletRequest request() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            throw new ProfilesAccessException("Local demonstration requires an HTTP request", true);
        }
        HttpServletRequest request = attrs.getRequest();
        String remote = request.getRemoteAddr();
        if (!"127.0.0.1".equals(remote) && !"0:0:0:0:0:0:0:1".equals(remote) && !"::1".equals(remote)) {
            throw new ProfilesAccessException("Local demonstration is restricted to loopback", true);
        }
        return request;
    }
    private static String role() {
        String role = request().getHeader("X-Rumbo-Demo-Role");
        if ("tutor".equalsIgnoreCase(role)) return "TUTOR";
        if ("driver".equalsIgnoreCase(role)) return "DRIVER";
        throw new ProfilesAccessException("Choose tutor or driver demo role locally", true);
    }
    @Override
    public Long currentAccountId() {
        return "TUTOR".equals(role()) ? TUTOR_ACCOUNT_ID : DRIVER_ACCOUNT_ID;
    }
    @Override
    public boolean hasRole(String name) {
        return !"ADMIN".equalsIgnoreCase(name) && role().equalsIgnoreCase(name);
    }
}
