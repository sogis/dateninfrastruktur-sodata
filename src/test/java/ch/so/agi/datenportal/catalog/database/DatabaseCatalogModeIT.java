package ch.so.agi.datenportal.catalog.database;
import java.util.*;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class DatabaseCatalogModeIT {
    @Test void twoRealInstancesReadCommittedAdminChangesWithoutSnapshotAndFailClosedOnDatabaseOutage()throws Exception{
        var data=DatabaseFixture.data();try(var first=DatabaseFixture.start();var second=DatabaseFixture.start();var admin=DatabaseFixture.source("postgres","DI_PUBLIC_TEST_ADMIN_PASSWORD")){
            String a=DatabaseFixture.base(first),b=DatabaseFixture.base(second),path="/datasets/"+data.get("openIdentifier");
            assertEquals(200,DatabaseFixture.get(a,path).statusCode());assertEquals(200,DatabaseFixture.get(b,"/actuator/health/readiness").statusCode());
            var process=new ProcessBuilder("python3","scripts/test_management_services.py","*DatabaseCatalogMutationIT").directory(Path.of(System.getenv("DI_ADMIN_SOURCE")).toFile()).redirectErrorStream(true).start();String output=new String(process.getInputStream().readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);assertEquals(0,process.waitFor(),"Actual admin Cayenne mutation failed; see own ADMIN .runtime/management-services.log");
            assertTrue(DatabaseFixture.get(a,path).body().contains("MA-D Aktualisiert ohne Reload"));assertTrue(DatabaseFixture.get(b,path).body().contains("MA-D Aktualisiert ohne Reload"));
            try(var c=admin.getConnection();var s=c.createStatement()){
                // This is a dedicated role in this owned database/container, never the user's stack.
                s.execute("ALTER ROLE di_public NOLOGIN");s.execute("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE usename='di_public' AND datname=current_database() AND pid<>pg_backend_pid()");
            }
            try{
                assertEquals(503,DatabaseFixture.get(a,path).statusCode());assertEquals(503,DatabaseFixture.get(b,path).statusCode());assertEquals(503,DatabaseFixture.get(a,"/actuator/health/readiness").statusCode());assertEquals(200,DatabaseFixture.get(a,"/actuator/health/liveness").statusCode());
            }finally{try(var c=admin.getConnection();var s=c.createStatement()){s.execute("ALTER ROLE di_public LOGIN");}}
        }
    }
}
