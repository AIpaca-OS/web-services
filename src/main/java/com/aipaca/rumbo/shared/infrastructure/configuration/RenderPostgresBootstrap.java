package com.aipaca.rumbo.shared.infrastructure.configuration;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Converts the Render-managed PostgreSQL connection string into JDBC
 * properties before Spring Boot creates its datasource. No credentials
 * are logged or checked into the repository.
 */
public final class RenderPostgresBootstrap {
    private RenderPostgresBootstrap() {}

    public static void configureFromEnvironment() {
        String renderUrl = System.getenv("RENDER_POSTGRES_URL");
        if (renderUrl == null || renderUrl.isBlank() ||
                System.getenv("SPRING_DATASOURCE_URL") != null) {
            return;
        }
        configure(renderUrl);
    }

    static void configure(String connectionString) {
        URI uri = URI.create(connectionString);
        if (!"postgresql".equalsIgnoreCase(uri.getScheme())
                && !"postgres".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("Render database must use PostgreSQL");
        }
        if (uri.getHost() == null || uri.getRawUserInfo() == null
                || uri.getRawPath() == null || uri.getRawPath().length() < 2) {
            throw new IllegalArgumentException("Invalid Render PostgreSQL connection string");
        }
        String userInfo = uri.getRawUserInfo();
        int separator = userInfo.indexOf(':');
        if (separator <= 0) {
            throw new IllegalArgumentException("Render PostgreSQL credentials are missing");
        }
        String username = decode(userInfo.substring(0, separator));
        String password = decode(userInfo.substring(separator + 1));
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port
                + uri.getRawPath();
        if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) {
            jdbcUrl += "?" + uri.getRawQuery();
        }

        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", username);
        System.setProperty("spring.datasource.password", password);
    }

    private static String decode(String raw) {
        return URLDecoder.decode(raw.replace("+", "%2B"), StandardCharsets.UTF_8);
    }
}
