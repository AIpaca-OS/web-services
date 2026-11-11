package com.aipaca.rumbo.shared.infrastructure.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RenderPostgresBootstrapTests {
    @Test
    void convertsRenderUriToJdbcWithoutLosingEncodedPassword() {
        String oldUrl = System.getProperty("spring.datasource.url");
        String oldUser = System.getProperty("spring.datasource.username");
        String oldPassword = System.getProperty("spring.datasource.password");
        try {
            RenderPostgresBootstrap.configure(
                    "postgresql://rumbo:s%40cr%3Aet@db.internal:5432/rumbo");
            assertEquals("jdbc:postgresql://db.internal:5432/rumbo",
                    System.getProperty("spring.datasource.url"));
            assertEquals("rumbo", System.getProperty("spring.datasource.username"));
            assertEquals("s@cr:et", System.getProperty("spring.datasource.password"));
        } finally {
            restore("spring.datasource.url", oldUrl);
            restore("spring.datasource.username", oldUser);
            restore("spring.datasource.password", oldPassword);
        }
    }

    @Test
    void rejectsNonPostgresUris() {
        assertThrows(IllegalArgumentException.class, () ->
                RenderPostgresBootstrap.configure("mysql://user:pass@localhost/db"));
    }

    private static void restore(String name, String value) {
        if (value == null) System.clearProperty(name);
        else System.setProperty(name, value);
    }
}
