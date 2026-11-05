package ch.so.agi.datenportal.admin.actuator;

import ch.so.agi.datenportal.catalog.database.JdbcCatalogRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("catalog")
public final class CatalogReadinessHealthIndicator implements HealthIndicator {
    private final JdbcCatalogRepository repository;

    public CatalogReadinessHealthIndicator(JdbcCatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        return repository.contractReady() ? Health.up().build() : Health.down().build();
    }
}
