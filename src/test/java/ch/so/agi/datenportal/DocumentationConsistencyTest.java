package ch.so.agi.datenportal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DocumentationConsistencyTest {

    @Test
    void configurationDocumentsDatabaseActuatorAndSecurityDefaults() throws IOException {
        String configuration = Files.readString(Path.of("docs/configuration.md"));

        assertThat(configuration)
                .contains("DATENINFRASTRUKTUR_CATALOG_DATABASE_JDBCURL", "di_public")
                .contains("/actuator/health")
                .contains("/actuator/health/liveness")
                .contains("/actuator/health/readiness")
                .contains("/actuator/info")
                .contains("spring.web.error.include-stacktrace")
                .contains("X-Content-Type-Options")
                .contains("Content-Security-Policy");
    }
}
