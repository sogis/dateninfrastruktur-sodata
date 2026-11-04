package ch.so.agi.datenportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class)
public class DatenportalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatenportalApplication.class, args);
    }
}
