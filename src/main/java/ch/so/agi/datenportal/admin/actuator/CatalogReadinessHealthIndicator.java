package ch.so.agi.datenportal.admin.actuator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.health.contributor.*;
import org.springframework.stereotype.Component;
import ch.so.agi.datenportal.catalog.database.JdbcCatalogRepository;
@Component("catalog")
public final class CatalogReadinessHealthIndicator implements HealthIndicator {
    private final ObjectProvider<JdbcCatalogRepository> database;
    private final ObjectProvider<CatalogSnapshotHealthIndicator> snapshot;
    private final ObjectProvider<CatalogReloadHealthIndicator> reload;
    private final ObjectProvider<CatalogSearchIndexHealthIndicator> index;
    public CatalogReadinessHealthIndicator(ObjectProvider<JdbcCatalogRepository> database,ObjectProvider<CatalogSnapshotHealthIndicator> snapshot,ObjectProvider<CatalogReloadHealthIndicator> reload,ObjectProvider<CatalogSearchIndexHealthIndicator> index){this.database=database;this.snapshot=snapshot;this.reload=reload;this.index=index;}
    public Health health(){var db=database.getIfAvailable();if(db!=null)return db.contractReady()?Health.up().build():Health.down().build();
        var s=snapshot.getIfAvailable();var r=reload.getIfAvailable();var i=index.getIfAvailable();
        return s!=null && r!=null && i!=null && Status.UP.equals(s.health().getStatus()) && Status.UP.equals(r.health().getStatus()) && Status.UP.equals(i.health().getStatus())?Health.up().build():Health.down().build();
    }
}
