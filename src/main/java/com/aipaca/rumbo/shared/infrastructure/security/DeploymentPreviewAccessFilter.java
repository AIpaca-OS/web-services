package com.aipaca.rumbo.shared.infrastructure.security;

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
 * A temporary access gate for the public AV2 preview.
 *
 * NOT IAM: a shared preview key provides no driver/tutor identity, role checks
 * or row-level authorization. Only use fictional data. Replace it with verified
 * JWT and RBAC before handling real guardian/student data.
 */
@Component
@Profile("prod")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DeploymentPreviewAccessFilter extends OncePerRequestFilter {
    private final String configuredKey;
    private final boolean publicDemoEnabled;

    public DeploymentPreviewAccessFilter(
            @Value("${DEPLOYMENT_ACCESS_TOKEN:}") String configuredKey,
            @Value("${BILLING_PUBLIC_DEMO_ENABLED:false}") boolean publicDemoEnabled) {
        this.configuredKey = configuredKey;
        this.publicDemoEnabled = publicDemoEnabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (!requiresKey(path, method)) {
            chain.doFilter(request, response);
            return;
        }
        if (configuredKey == null || configuredKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Public preview access is not configured");
            return;
        }
        String suppliedKey = request.getHeader("X-Rumbo-Preview-Key");
        if (suppliedKey == null || !MessageDigest.isEqual(
                configuredKey.getBytes(StandardCharsets.UTF_8),
                suppliedKey.getBytes(StandardCharsets.UTF_8))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "A private preview key is required");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean requiresKey(String path, String method) {
        if ("OPTIONS".equalsIgnoreCase(method)) return false;
        // Public demo endpoints use individually signed, expiring bearer sessions.
        // They never authorize the existing private /api/v1/subscriptions routes.
        if (publicDemoEnabled && path.startsWith("/api/v1/billing-demo/")) return false;
        if (!path.startsWith("/api/v1/")) return false;
        // Public plan catalog is safe to browse. All other APIs can disclose
        // routes, students, driver activity or billing details until IAM ships.
        return !("GET".equalsIgnoreCase(method)
                && path.matches("/api/v1/plans(?:/[0-9]+)?"));
    }
}
