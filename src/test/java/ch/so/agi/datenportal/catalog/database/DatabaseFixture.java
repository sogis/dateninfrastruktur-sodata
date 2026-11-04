package ch.so.agi.datenportal.catalog.database;
import java.nio.file.*;
import java.time.Clock;
import java.util.*;
import java.net.*;
import java.net.http.*;
import com.zaxxer.hikari.HikariDataSource;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ch.so.agi.datenportal.DatenportalApplication;
final class DatabaseFixture {
    static Map<String,String> data(){try{return new ObjectMapper().readValue(Files.readString(Path.of(System.getenv("DI_PORTAL_FIXTURE"))),Map.class);}catch(Exception e){throw new IllegalStateException("Run the explicitly owned portal harness",e);}}
    static HikariDataSource source(String role,String passwordKey){var ds=new HikariDataSource();ds.setJdbcUrl(System.getenv("DI_PUBLIC_TEST_URL"));ds.setUsername(role);ds.setPassword(System.getenv(passwordKey));ds.setMaximumPoolSize(3);ds.setMinimumIdle(0);ds.setConnectionTimeout(3000);return ds;}
    static ConfigurableApplicationContext start(){return new SpringApplicationBuilder(DatenportalApplication.class).run(
        "--server.address=127.0.0.1","--server.port=0","--dateninfrastruktur.catalog.source-type=database",
        "--dateninfrastruktur.catalog.classpath-location=THIS_FILE_MUST_NOT_BE_READ.xtf",
        "--dateninfrastruktur.catalog.http-url=http://127.0.0.1:1/no-manifest",
        "--dateninfrastruktur.catalog.database.jdbc-url="+System.getenv("DI_PUBLIC_TEST_URL"),
        "--dateninfrastruktur.catalog.database.username=di_public",
        "--dateninfrastruktur.catalog.database.password="+System.getenv("DI_PUBLIC_TEST_PASSWORD"),
        "--dateninfrastruktur.explore.enabled=false","--dateninfrastruktur.web-components.enabled=false");}
    static String base(ConfigurableApplicationContext context){return "http://127.0.0.1:"+context.getEnvironment().getProperty("local.server.port");}
    static HttpResponse<String> get(String base,String path)throws Exception{return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(base+path)).GET().build(),HttpResponse.BodyHandlers.ofString());}
}
