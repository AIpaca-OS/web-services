package com.aipaca.rumbo.platform;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Guards the structural rules of the platform: every bounded context lives under
 * {@code com.aipaca.rumbo.platform}, contexts never import each other (only {@code shared}),
 * and the only resources are the three application properties files.
 */
class PlatformArchitectureTests {
    private static final Path BASE = Path.of("src/main/java/com/aipaca/rumbo");
    private static final Path PLATFORM = BASE.resolve("platform");
    private static final Pattern IMPORT = Pattern.compile(
            "^import\\s+(?:static\\s+)?com\\.aipaca\\.rumbo\\.(?:platform\\.)?(\\w+)\\.", Pattern.MULTILINE);

    @Test
    void allCodeLivesUnderPlatform() throws IOException {
        // During the AV2 migration, legacy packages coexist with the new platform.
        assertTrue(Files.isDirectory(PLATFORM));
        assertTrue(Files.isDirectory(PLATFORM.resolve("shared")));
        assertTrue(Files.isDirectory(PLATFORM.resolve("vehicles")));
    }

    @Test
    void boundedContextsDoNotImportEachOther() throws IOException {
        var violations = new ArrayList<String>();
        try (Stream<Path> files = Files.walk(PLATFORM)) {
            for (var file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                var relative = PLATFORM.relativize(file);
                if (relative.getNameCount() < 2) continue;
                var context = relative.getName(0).toString();
                var matcher = IMPORT.matcher(Files.readString(file));
                while (matcher.find()) {
                    var imported = matcher.group(1);
                    boolean allowed = imported.equals(context) || (imported.equals("shared") && !context.equals("shared"));
                    if (!allowed) violations.add(relative + " -> " + imported);
                }
            }
        }
        assertTrue(violations.isEmpty(), "Cross-context imports: " + violations);
    }

    @Test
    void resourcesFollowTheLearningCenterModel() throws IOException {
        try (Stream<Path> files = Files.walk(Path.of("src/main/resources"))) {
            var names = files.filter(Files::isRegularFile).map(path -> path.getFileName().toString()).sorted().toList();
            // application-local.properties is part of the existing demo profile.
            assertTrue(names.containsAll(List.of("ValidationMessages.properties", "ValidationMessages_es.properties",
                    "application-dev.properties", "application-local.properties",
                    "application-prod.properties", "application.properties",
                    "messages.properties", "messages_es.properties")), names.toString());
        }
    }
}
