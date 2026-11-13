package com.aipaca.rumbo.platform.billing.infrastructure.security;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Billing-only production access guard for the new Platform structure.
 * Its shared preview filter was removed during restructuring.
 *
 * Existing private subscription and plan-management endpoints require the
 * private preview key until verified driver IAM is available. The public
 * catalog stays read-only and the isolated billing-demo endpoints authenticate
 * using their own per-browser signed tokens.
 *
 * This filter deliberately never changes access to other bounded contexts.
 */
@Component
@Profile("prod")
@ConditionalOnProperty(name = "BILLING_PRIVATE_ACCESS_ENABLED", havingValue = "true")
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class BillingPrivateApiAccessFilter extends OncePerRequestFilter {
    private final String configuredKey;

    public BillingPrivateApiAccessFilter(
            @Value("${DEPLOYMENT_ACCESS_TOKEN:}") String configuredKey) {
        this.configuredKey = configuredKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        if (!isProtectedBillingRequest(request.getRequestURI(), request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        if (configuredKey == null || configuredKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Private Billing preview access is not configured");
            return;
        }
        var supplied = request.getHeader("X-Rumbo-Preview-Key");
        if (supplied == null || !MessageDigest.isEqual(
                configuredKey.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Private Billing preview access requires a valid key");
            return;
        }
        chain.doFilter(request, response);
    }

    static boolean isProtectedBillingRequest(String path, String method) {
        if ("OPTIONS".equalsIgnoreCase(method) || !path.startsWith("/api/v1/")) {
            return false;
        }
        if (path.startsWith("/api/v1/billing-demo/")) {
            // These endpoints authenticate separately via signed session tokens.
            return false;
        }
        boolean plans = path.equals("/api/v1/plans") || path.startsWith("/api/v1/plans/");
        boolean subscriptions = path.equals("/api/v1/subscriptions")
                || path.startsWith("/api/v1/subscriptions/");
        if (!plans && !subscriptions) {
            return false; // Do not interfere with Profiles, Routes, etc.
        }
        return !(plans && "GET".equalsIgnoreCase(method)
                && path.matches("/api/v1/plans(?:/[0-9]+)?"));
    }
}
