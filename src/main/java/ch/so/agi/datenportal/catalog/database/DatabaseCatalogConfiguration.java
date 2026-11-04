package ch.so.agi.datenportal.catalog.database;
import java.time.Clock;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
@Configuration @ConditionalOnProperty(name="dateninfrastruktur.catalog.source-type",havingValue="database")
public class DatabaseCatalogConfiguration {
    @Bean(destroyMethod="close") @ConfigurationProperties("dateninfrastruktur.catalog.database")
    HikariDataSource publicCatalogDataSource(){var source=new HikariDataSource();source.setMaximumPoolSize(8);source.setMinimumIdle(0);source.setReadOnly(true);source.setConnectionTimeout(3000);source.setPoolName("public-catalog-readonly");return source;}
    @Bean JdbcCatalogRepository jdbcCatalogRepository(HikariDataSource source,Clock clock){return new JdbcCatalogRepository(source,clock);}
    @Bean CatalogQueryService catalogQueryService(JdbcCatalogRepository repository){return new CatalogQueryService(repository);}
    @Bean org.springframework.boot.ApplicationRunner verifyPublicDatabase(JdbcCatalogRepository repository){return args->{if(!repository.contractReady())throw new IllegalStateException("Public catalog requires its dedicated read-only role and current SQL views; no snapshot fallback");};}
}
