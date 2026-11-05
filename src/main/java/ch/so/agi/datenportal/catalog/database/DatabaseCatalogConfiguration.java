package ch.so.agi.datenportal.catalog.database;
import java.time.Clock;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ch.so.agi.datenportal.config.SecurityCspProperties;
import ch.so.agi.datenportal.config.WebComponentsProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
@Configuration
@EnableConfigurationProperties({SecurityCspProperties.class, WebComponentsProperties.class})
public class DatabaseCatalogConfiguration {
    @Bean Clock catalogClock(){return Clock.systemUTC();}
    @Bean(destroyMethod="close") @ConfigurationProperties("dateninfrastruktur.catalog.database")
    HikariDataSource publicCatalogDataSource(){var source=new HikariDataSource();source.setMaximumPoolSize(8);source.setMinimumIdle(0);source.setReadOnly(true);source.setConnectionTimeout(3000);source.setPoolName("public-catalog-readonly");return source;}
    @Bean JdbcCatalogRepository jdbcCatalogRepository(HikariDataSource source,Clock clock){return new JdbcCatalogRepository(source,clock);}
    @Bean CatalogQueryService catalogQueryService(JdbcCatalogRepository repository){return new CatalogQueryService(repository);}
    @Bean org.springframework.boot.ApplicationRunner verifyPublicDatabase(JdbcCatalogRepository repository){return args->{if(!repository.contractReady())throw new IllegalStateException("Public catalog requires its dedicated read-only role and current SQL views");};}
}
