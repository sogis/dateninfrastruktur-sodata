package ch.so.agi.datenportal;

import ch.so.agi.datenportal.config.JteRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication(exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class)
@ImportRuntimeHints(JteRuntimeHints.class)
public class DatenportalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatenportalApplication.class, args);
    }
}
